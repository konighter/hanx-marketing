package com.hzltd.module.erplus.ai.mas.execution;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import com.hzltd.module.erplus.ai.mas.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.memory.LocalNodeMemory;
import com.hzltd.module.erplus.ai.mas.persistence.MasCheckpointService;
import com.hzltd.module.erplus.ai.mas.spi.execution.NodeRunner;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasSessionMemory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * Executes a single graph node through its lifecycle.
 */
@Slf4j
public class NodeExecutor implements Callable<String> {

    private final GraphNode node;
    private final MasSessionMemory sessionMemory;
    private final MasCheckpointService checkpointService;
    private final MasMemoryManager memoryManager;
    private final MasEventLogService eventLogService;
    private final NodeRunnerProvider runnerProvider;
    
    public NodeExecutor(GraphNode node, 
                        MasSessionMemory sessionMemory, 
                        MasCheckpointService checkpointService,
                        MasMemoryManager memoryManager,
                        MasEventLogService eventLogService,
                        NodeRunnerProvider runnerProvider) {
        this.node = node;
        this.sessionMemory = sessionMemory;
        this.checkpointService = checkpointService;
        this.memoryManager = memoryManager;
        this.eventLogService = eventLogService;
        this.runnerProvider = runnerProvider;
    }

    @Override
    public String call() throws Exception {
        BaseAgent assignedAgent = node.getAgent();
        log.info("[NodeExecutor] Starting node: {} with Agent: {}", node.getNodeId(), assignedAgent.getRoleName());
        String sessionId = sessionMemory.getSessionId();
        String nodeId = node.getNodeId();

        if (eventLogService != null) {
            eventLogService.logEvent(sessionId, nodeId, "START", "Starting Node: " + nodeId);
        }
        
        long startTime = System.currentTimeMillis();
        String result = "";
        
        node.setStartTime(startTime);
        node.setStatus(GraphNode.NodeStatus.RUNNING);
        
        RetryPolicy retryPolicy = node.getRetryPolicy();
        if (retryPolicy == null) retryPolicy = RetryPolicy.none();
        
        int attempts = 0;
        long currentBackoff = retryPolicy.getInitialBackoffMs();

        // 1. Initialize Local Scope Memory
        LocalNodeMemory nodeMemory = new LocalNodeMemory(node.getNodeId(), sessionMemory);
        
        if (checkpointService != null) {
            MasTaskHistoryDO existing = checkpointService.findLatestCheckpoint(sessionMemory.getSessionId(), node.getNodeId());
            if (existing != null) {
                log.info("[NodeExecutor] Found existing checkpoint for node: {}. Recovering.", node.getNodeId());
                result = existing.getResult();
                node.setResult(result);
                node.setStatus(GraphNode.NodeStatus.SUCCESS);
                node.setEndTime(System.currentTimeMillis());
                
                nodeMemory.put(node.getNodeId() + "_" + assignedAgent.getRoleName() + "_output", result);
                nodeMemory.mergeToGlobal();

                if (eventLogService != null) {
                    eventLogService.logEvent(sessionId, nodeId, "RECOVERED", "Skipped execution — restored from checkpoint");
                }
                return result;
            }
        }

        try {
            while (attempts <= retryPolicy.getMaxAttempts()) {
                attempts++;
                try {
                    log.info("[NodeExecutor] Delegating execution to NodeRunner for agent: {}", assignedAgent.getRoleName());
                    if (eventLogService != null) eventLogService.logEvent(sessionId, nodeId, "AGENT_EXECUTION", "Starting agent execution");
                    
                    NodeRunner selectedRunner = runnerProvider.getRunner(node.getNodeType());
                    
                    result = selectedRunner.run(node, nodeMemory);
                    nodeMemory.put("execution_result", result);
                    break;

                } catch (Throwable e) {
                    if (attempts >= retryPolicy.getMaxAttempts()) {
                        node.setStatus(GraphNode.NodeStatus.FAILED);
                        node.setEndTime(System.currentTimeMillis());
                        log.error("[NodeExecutor] Max attempts reached for node: {}. Final failure.", node.getNodeId(), e);
                        throw e;
                    }
                    node.setStatus(GraphNode.NodeStatus.RETRYING);
                    log.warn("[NodeExecutor] Attempt {} failed for node: {}. Retrying in {}ms... Error: {}", 
                            attempts, node.getNodeId(), currentBackoff, e.getMessage());
                    if (eventLogService != null) {
                        eventLogService.logEvent(sessionId, nodeId, "RETRYING", "Attempt " + attempts + " failed. Next in " + currentBackoff + "ms");
                    }
                    
                    Thread.sleep(currentBackoff);
                    currentBackoff = Math.min((long) (currentBackoff * retryPolicy.getBackoffMultiplier()), retryPolicy.getMaxBackoffMs());
                }
            }
            
            node.setResult(result);
            node.setStatus(GraphNode.NodeStatus.SUCCESS);
            node.setEndTime(System.currentTimeMillis());

            nodeMemory.put(node.getNodeId() + "_" + assignedAgent.getRoleName() + "_output", result);
            if (eventLogService != null) eventLogService.logEvent(sessionId, nodeId, "DONE", "Execution Successful after " + attempts + " attempts");
            
            nodeMemory.mergeToGlobal();
            
            if (eventLogService != null) {
                eventLogService.logStateSnapshot(sessionId, nodeId, nodeMemory.asMap());
            }
            
            if (memoryManager != null) {
                memoryManager.saveToDb(sessionMemory.getSessionId());
            }
        } catch (Throwable e) {
            log.error("[NodeExecutor] Execution failed for node: {} after {} attempts", node.getNodeId(), attempts, e);
            if (eventLogService != null) {
                eventLogService.logEvent(sessionId, nodeId, "ERROR", e.getMessage());
            }
            node.setResult(e.getMessage());
            throw new Exception(e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            if (checkpointService != null) {
                checkpointService.saveCheckpoint(sessionMemory.getSessionId(), node.getNodeId(), 
                        assignedAgent.getRoleName(), assignedAgent.getInstruction(), node.getResult(), 
                        node.getStatus().name(), nodeMemory, duration);
            }
        }
        
        log.info("[NodeExecutor] Completed node: {}", node.getNodeId());
        return node.getResult();
    }
}
