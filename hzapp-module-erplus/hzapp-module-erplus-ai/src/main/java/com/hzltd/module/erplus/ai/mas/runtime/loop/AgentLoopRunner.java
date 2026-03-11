package com.hzltd.module.erplus.ai.mas.runtime.loop;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LocalLoopMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * Executes a single graph node through its lifecycle (Think -> Plan -> Execute -> Review).
 */
@Slf4j
public class AgentLoopRunner implements Callable<String> {

    private final GraphNode node;
    private final GlobalSessionMemory sessionMemory;
    private final MasCheckpointService checkpointService;
    private final MasMemoryService memoryService;
    private final MasEventLogService eventLogService;
    
    public AgentLoopRunner(GraphNode node, 
                           GlobalSessionMemory sessionMemory, 
                           MasCheckpointService checkpointService,
                           MasMemoryService memoryService,
                           MasEventLogService eventLogService) {
        this.node = node;
        this.sessionMemory = sessionMemory;
        this.checkpointService = checkpointService;
        this.memoryService = memoryService;
        this.eventLogService = eventLogService;
    }

    @Override
    public String call() throws Exception {
        BaseAgent assignedAgent = node.getAgent();
        log.info("[AgentLoopRunner] Starting loop: {} with Agent: {}", node.getLoopId(), assignedAgent.getRoleName());
        String sessionId = sessionMemory.getSessionId();
        String loopId = node.getLoopId();

        if (eventLogService != null) {
            eventLogService.logEvent(sessionId, loopId, "START", "Starting Loop: " + loopId);
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
        LocalLoopMemory loopMemory = new LocalLoopMemory(node.getLoopId(), sessionMemory);
        
        // Recovery check
        if (checkpointService != null) {
            MasTaskHistoryDO existing = checkpointService.findLatestCheckpoint(sessionMemory.getSessionId(), node.getLoopId());
            if (existing != null) {
                log.info("[AgentLoopRunner] Found existing checkpoint for node: {}. Resuming result.", node.getLoopId());
                result = existing.getResult();
                node.setResult(result);
                node.setStatus(GraphNode.NodeStatus.SUCCESS);
                node.setEndTime(System.currentTimeMillis());
                
                // Put output back into memory for downstream dependencies
                loopMemory.put(assignedAgent.getRoleName() + "_output", result);
                loopMemory.mergeToGlobal();
                return result;
            }
        }

        try {
            while (attempts <= retryPolicy.getMaxAttempts()) {
                attempts++;
                try {
                    log.info("[AgentLoopRunner] Delegating execution to agent: {}", assignedAgent.getRoleName());
                    if (eventLogService != null) eventLogService.logEvent(sessionId, loopId, "AGENT_EXECUTION", "Starting agent execution");
                    
                    result = assignedAgent.execute(loopMemory);
                    loopMemory.put("execution_result", result);


                    // If we reach here, execution (and review) succeeded
                    break;

                } catch (Exception e) {
                    if (attempts >= retryPolicy.getMaxAttempts()) {
                        node.setStatus(GraphNode.NodeStatus.FAILED);
                        node.setEndTime(System.currentTimeMillis());
                        log.error("[AgentLoopRunner] Max attempts reached for node: {}. Final failure.", node.getLoopId(), e);
                        throw e;
                    }
                    node.setStatus(GraphNode.NodeStatus.RETRYING);
                    log.warn("[AgentLoopRunner] Attempt {} failed for node: {}. Retrying in {}ms... Error: {}", 
                            attempts, node.getLoopId(), currentBackoff, e.getMessage());
                    if (eventLogService != null) {
                        eventLogService.logEvent(sessionId, loopId, "RETRYING", "Attempt " + attempts + " failed. Next in " + currentBackoff + "ms");
                    }
                    
                    Thread.sleep(currentBackoff);
                    currentBackoff = Math.min((long) (currentBackoff * retryPolicy.getBackoffMultiplier()), retryPolicy.getMaxBackoffMs());
                }
            }
            
            node.setResult(result);
            node.setStatus(GraphNode.NodeStatus.SUCCESS);
            node.setEndTime(System.currentTimeMillis());

            loopMemory.put(assignedAgent.getRoleName() + "_output", result);
            if (eventLogService != null) eventLogService.logEvent(sessionId, loopId, "DONE", "Execution Successful after " + attempts + " attempts");
            
            // Finally, merge to global session memory
            loopMemory.mergeToGlobal();
            
            // Phase 8: Capture final state snapshot for visual playback
            if (eventLogService != null) {
                eventLogService.logStateSnapshot(sessionId, loopId, loopMemory.asMap());
            }
            
            if (memoryService != null) {
                memoryService.saveToDb(sessionMemory.getSessionId());
            }
        } catch (Exception e) {
            log.error("[AgentLoopRunner] Execution failed for node: {} after {} attempts", node.getLoopId(), attempts, e);
            if (eventLogService != null) {
                eventLogService.logEvent(sessionId, loopId, "ERROR", e.getMessage());
            }
            node.setResult(e.getMessage());
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            // Record execution in task history if checkpoint service is available
            if (checkpointService != null) {
                checkpointService.saveCheckpoint(sessionMemory.getSessionId(), node.getLoopId(), 
                        assignedAgent.getRoleName(), assignedAgent.getInstruction(), node.getResult(), 
                        node.getStatus().name(), loopMemory, duration);
            }
        }
        
        log.info("[AgentLoopRunner] Completed loop: {}", node.getLoopId());
        return node.getResult();
    }
}
