package com.hzltd.module.erplus.ai.mas.runtime.execution;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LocalNodeMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * Executes a single graph node through its lifecycle:
 * <ul>
 *   <li>Initialize isolated local memory</li>
 *   <li>Check for existing checkpoint (recovery)</li>
 *   <li>Delegate execution to {@link NodeRunner}</li>
 *   <li>Handle retries via {@link RetryPolicy}</li>
 *   <li>Merge results to global memory</li>
 *   <li>Persist checkpoint and event logs</li>
 * </ul>
 */
@Slf4j
public class NodeExecutor implements Callable<String> {

    private final GraphNode node;
    private final GlobalSessionMemory sessionMemory;
    private final MasCheckpointService checkpointService;
    private final MasMemoryService memoryService;
    private final MasEventLogService eventLogService;
    private final NodeRunner nodeRunner;
    private final NodeRunner reactNodeRunner;
    
    public NodeExecutor(GraphNode node, 
                        GlobalSessionMemory sessionMemory, 
                        MasCheckpointService checkpointService,
                        MasMemoryService memoryService,
                        MasEventLogService eventLogService,
                        NodeRunner nodeRunner,
                        NodeRunner reactNodeRunner) {
        this.node = node;
        this.sessionMemory = sessionMemory;
        this.checkpointService = checkpointService;
        this.memoryService = memoryService;
        this.eventLogService = eventLogService;
        this.nodeRunner = nodeRunner;
        this.reactNodeRunner = reactNodeRunner;
    }

    /**
     * Backward-compatible constructor (no ReAct runner).
     */
    public NodeExecutor(GraphNode node, 
                        GlobalSessionMemory sessionMemory, 
                        MasCheckpointService checkpointService,
                        MasMemoryService memoryService,
                        MasEventLogService eventLogService,
                        NodeRunner nodeRunner) {
        this(node, sessionMemory, checkpointService, memoryService, eventLogService, nodeRunner, null);
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
        
        // Recovery check
        if (checkpointService != null) {
            MasTaskHistoryDO existing = checkpointService.findLatestCheckpoint(sessionMemory.getSessionId(), node.getNodeId());
            if (existing != null) {
                log.info("[NodeExecutor] Found existing checkpoint for node: {}. Recovering with previous SUCCESS result.", node.getNodeId());
                result = existing.getResult();
                node.setResult(result);
                node.setStatus(GraphNode.NodeStatus.SUCCESS);
                node.setEndTime(System.currentTimeMillis());
                
                // Put output back into memory for downstream dependencies
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
                    
                    // Select runner based on node type
                    NodeRunner selectedRunner = nodeRunner; // default: SIMPLE
                    if (node.getNodeType() == GraphNode.NodeType.REACT && reactNodeRunner != null) {
                        selectedRunner = reactNodeRunner;
                        log.info("[NodeExecutor] Using ReActNodeRunner for REACT node: {}", nodeId);
                    }
                    result = selectedRunner.run(node, nodeMemory);
                    nodeMemory.put("execution_result", result);

                    // If we reach here, execution succeeded
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
            
            // Merge to global session memory
            nodeMemory.mergeToGlobal();
            
            // Capture final state snapshot for visual playback
            if (eventLogService != null) {
                eventLogService.logStateSnapshot(sessionId, nodeId, nodeMemory.asMap());
            }
            
            if (memoryService != null) {
                memoryService.saveToDb(sessionMemory.getSessionId());
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
            // Record execution in task history if checkpoint service is available
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
