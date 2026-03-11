package com.hzltd.module.erplus.ai.mas.runtime.loop;

import com.hzltd.module.erplus.ai.mas.runtime.communication.A2AMessageBus;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * Manages the Directed Acyclic Graph (DAG) of AgentLoops and evaluates dependencies to execute them in parallel.
 */
@Slf4j
public class LoopGraphManager {

    private final GlobalSessionMemory sessionMemory;
    private final Map<String, GraphNode> nodes = new ConcurrentHashMap<>();
    private final MasCheckpointService checkpointService;
    private final MasMemoryService memoryService;
    private final MasEventLogService eventLogService;
    private final A2AMessageBus messageBus;
    private final ExecutorService executor;
    
    public LoopGraphManager(GlobalSessionMemory sessionMemory, 
                            MasCheckpointService checkpointService,
                            MasMemoryService memoryService,
                            MasEventLogService eventLogService,
                            A2AMessageBus messageBus) {
        this.sessionMemory = sessionMemory;
        this.checkpointService = checkpointService;
        this.memoryService = memoryService;
        this.eventLogService = eventLogService;
        this.messageBus = messageBus;
        // Bounded thread pool to prevent resource exhaustion
        this.executor = new ThreadPoolExecutor(10, 50, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
    }

    public void addNode(GraphNode node) {
        nodes.put(node.getLoopId(), node);
    }

    /**
     * Executes the graph honoring dependencies.
     */
    public void executeGraph() {
        log.info("[LoopGraphManager] Starting Graph Execution for Session: {}", sessionMemory.getSessionId());
        
        Map<String, Future<String>> futures = new HashMap<>();
        Map<String, String> results = new HashMap<>();
        
        long graphTimeout = 600000; // 10 minutes session timeout
        long startTime = System.currentTimeMillis();
        Set<String> completed = ConcurrentHashMap.newKeySet();
        Set<String> inProgress = ConcurrentHashMap.newKeySet();
        
        while (true) {
            boolean allFinished = true;
            for (GraphNode node : nodes.values()) {
                if (node.getStatus() == GraphNode.NodeStatus.PENDING || node.getStatus() == GraphNode.NodeStatus.RUNNING || node.getStatus() == GraphNode.NodeStatus.RETRYING) {
                    allFinished = false;
                    break;
                }
            }
            if (allFinished) break;

            boolean progressMade = false;
            
            for (GraphNode node : nodes.values()) {
                String loopId = node.getLoopId();
                if (node.getStatus() == GraphNode.NodeStatus.SKIPPED || completed.contains(loopId) || inProgress.contains(loopId)) {
                    continue;
                }
                
                // Check if any dependency FAILED or was SKIPPED
                boolean anyDependencyFailed = false;
                for (String reqId : node.getRequires()) {
                    GraphNode reqNode = nodes.get(reqId);
                    if (reqNode != null && (reqNode.getStatus() == GraphNode.NodeStatus.FAILED || reqNode.getStatus() == GraphNode.NodeStatus.SKIPPED)) {
                        anyDependencyFailed = true;
                        break;
                    }
                }

                if (anyDependencyFailed) {
                    log.warn("[LoopGraphManager] Skipping node {} due to failed dependency", loopId);
                    node.setStatus(GraphNode.NodeStatus.SKIPPED);
                    completed.add(loopId);
                    progressMade = true;
                    continue;
                }
                
                // Check if all dependencies are SUCCESS
                boolean allDependenciesDone = true;
                for (String reqId : node.getRequires()) {
                    GraphNode reqNode = nodes.get(reqId);
                    if (reqNode == null || reqNode.getStatus() != GraphNode.NodeStatus.SUCCESS) {
                        allDependenciesDone = false;
                        break;
                    }
                }

                if (allDependenciesDone) {
                    log.info("[LoopGraphManager] Dispatching node: {}", loopId);
                    inProgress.add(loopId);
                    
                    AgentLoopRunner runner = new AgentLoopRunner(node, sessionMemory, 
                            checkpointService, memoryService, eventLogService);
                    Future<String> future = executor.submit(runner);
                    futures.put(loopId, future);
                    progressMade = true;
                }
            }
            
            // Check for completed futures
            Iterator<Map.Entry<String, Future<String>>> it = futures.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Future<String>> entry = it.next();
                if (entry.getValue().isDone()) {
                    try {
                        String result = entry.getValue().get(); 
                        results.put(entry.getKey(), result);
                        completed.add(entry.getKey());
                        inProgress.remove(entry.getKey());
                        it.remove();
                        progressMade = true;
                    } catch (Exception e) {
                        log.error("[LoopGraphManager] Execution failed for node: {}", entry.getKey(), e);
                        // We mark the node status as FAILED (already done in AgentLoopRunner)
                        // And we continue execution to allow other branches of the graph or skip logic to handle it
                        completed.add(entry.getKey()); // Mark as 'finished' so we don't spin on it
                        inProgress.remove(entry.getKey());
                        it.remove();
                        progressMade = true;
                    }
                }
            }
            
            int totalFinished = 0;
            for (GraphNode gn : nodes.values()) {
                if (gn.getStatus() == GraphNode.NodeStatus.SUCCESS || gn.getStatus() == GraphNode.NodeStatus.FAILED || gn.getStatus() == GraphNode.NodeStatus.SKIPPED) {
                    totalFinished++;
                }
            }
            if (!progressMade && inProgress.isEmpty() && totalFinished < nodes.size()) {
                log.error("[LoopGraphManager] Deadlock detected! Finished: {}, Total: {}", totalFinished, nodes.size());
                executor.shutdownNow();
                throw new IllegalStateException("Graph contains circular dependencies or unresolvable nodes");
            }

            if (System.currentTimeMillis() - startTime > graphTimeout) {
                log.error("[LoopGraphManager] Graph Execution TIMEOUT for Session: {}", sessionMemory.getSessionId());
                cancelAll(futures);
                throw new RuntimeException("Graph execution timeout");
            }
            
            try {
                Thread.sleep(100); // prevent tight spin loop
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                cancelAll(futures);
                return;
            }
        }
        
        // The class-level executor is managed externally or shut down when the application exits.
        // Do not shut down the shared executor here.
        log.info("[LoopGraphManager] Graph Execution Completed for Session: {}", sessionMemory.getSessionId());
    }

    private void cancelAll(Map<String, Future<String>> futures) {
        futures.values().forEach(f -> f.cancel(true));
    }
}
