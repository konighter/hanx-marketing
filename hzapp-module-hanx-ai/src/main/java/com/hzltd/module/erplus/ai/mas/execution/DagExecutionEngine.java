package com.hzltd.module.erplus.ai.mas.execution;

import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.persistence.MasCheckpointService;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasSessionMemory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * Event-driven DAG execution engine using {@link CompletionService} to
 * replace the original busy-wait polling loop.
 */
@Slf4j
public class DagExecutionEngine {

    private final MasSessionMemory sessionMemory;
    private final Map<String, GraphNode> nodes = new ConcurrentHashMap<>();
    private final MasCheckpointService checkpointService;
    private final MasMemoryManager memoryManager;
    private final MasEventLogService eventLogService;
    private final ExecutorService executor;
    private final NodeExecutorFactory nodeExecutorFactory;

    /** Default graph-level timeout: 10 minutes. */
    private static final long DEFAULT_GRAPH_TIMEOUT_MS = 600_000L;

    /** Poll interval when waiting for next node completion. */
    private static final long POLL_INTERVAL_MS = 500L;

    public DagExecutionEngine(MasSessionMemory sessionMemory,
                                MasCheckpointService checkpointService,
                                MasMemoryManager memoryManager,
                                MasEventLogService eventLogService,
                                ExecutorService executor,
                                NodeExecutorFactory nodeExecutorFactory) {
        this.sessionMemory = sessionMemory;
        this.checkpointService = checkpointService;
        this.memoryManager = memoryManager;
        this.eventLogService = eventLogService;
        this.executor = executor;
        this.nodeExecutorFactory = nodeExecutorFactory;
    }

    public void addNode(GraphNode node) {
        nodes.put(node.getNodeId(), node);
    }

    public DagExecutionResult execute() {
        log.info("[DagExecutionEngine] Starting execution of {} nodes", nodes.size());
        long startTime = System.currentTimeMillis();
        
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
        Set<String> submittedTasks = new HashSet<>();
        int completedCount = 0;
        int failedCount = 0;
        int totalNodes = nodes.size();
        List<DagExecutionResult.NodeFailureDetail> failures = new ArrayList<>();

        try {
            while (completedCount + failedCount < totalNodes) {
                // 1. Submit ready nodes
                for (GraphNode node : nodes.values()) {
                    if (node.getStatus() == GraphNode.NodeStatus.PENDING && isReady(node) && !submittedTasks.contains(node.getNodeId())) {
                        log.info("[DagExecutionEngine] Submitting node for execution: {}", node.getNodeId());
                        completionService.submit(nodeExecutorFactory.create(node, sessionMemory));
                        submittedTasks.add(node.getNodeId());
                    }
                }

                // 2. Wait for any node to complete
                Future<String> future = completionService.poll(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
                if (future != null) {
                    try {
                        future.get(); // Result is already saved in node object by NodeExecutor
                        completedCount++;
                    } catch (ExecutionException e) {
                        failedCount++;
                        log.error("[DagExecutionEngine] Node execution failed", e.getCause());
                        // Identify which node failed (this is tricky with poll, but we can check statuses)
                        failUnfinishedNode(failures);
                    }
                }

                // 3. Timeout check
                if (System.currentTimeMillis() - startTime > DEFAULT_GRAPH_TIMEOUT_MS) {
                    log.error("[DagExecutionEngine] Graph execution timed out!");
                    break;
                }
                
                // 4. Deadlock check (no tasks submitted and no progress)
                if (submittedTasks.size() < totalNodes && !hasReadyNodes()) {
                    log.error("[DagExecutionEngine] Deadlock detected or dependencies unsatisfiable!");
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("[DagExecutionEngine] Execution interrupted", e);
            Thread.currentThread().interrupt();
        }

        return DagExecutionResult.builder()
                .totalNodes(totalNodes)
                .successCount(completedCount)
                .failedCount(failedCount)
                .skippedCount(totalNodes - completedCount - failedCount)
                .failures(failures)
                .build();
    }

    private boolean isReady(GraphNode node) {
        for (String depId : node.getRequires()) {
            GraphNode dep = nodes.get(depId);
            if (dep == null || dep.getStatus() != GraphNode.NodeStatus.SUCCESS) {
                return false;
            }
        }
        return true;
    }

    private boolean hasReadyNodes() {
        return nodes.values().stream()
                .anyMatch(n -> n.getStatus() == GraphNode.NodeStatus.PENDING && isReady(n));
    }

    private void failUnfinishedNode(List<DagExecutionResult.NodeFailureDetail> failures) {
        for (GraphNode node : nodes.values()) {
            if (node.getStatus() == GraphNode.NodeStatus.FAILED) {
                // Check if we already recorded this failure
                boolean alreadyRecorded = failures.stream().anyMatch(f -> f.getNodeId().equals(node.getNodeId()));
                if (!alreadyRecorded) {
                    failures.add(DagExecutionResult.NodeFailureDetail.builder()
                            .nodeId(node.getNodeId())
                            .errorMessage(node.getResult())
                            .build());
                }
            }
        }
    }
}
