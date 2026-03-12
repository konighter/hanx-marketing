package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.erplus.ai.mas.runtime.agent.PlannerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.agent.ReviewerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.execution.DagExecutionEngine;
import com.hzltd.module.erplus.ai.mas.runtime.execution.DagExecutionResult;
import com.hzltd.module.erplus.ai.mas.runtime.execution.GraphNode;
import com.hzltd.module.erplus.ai.mas.runtime.execution.NodeRunner;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LocalNodeMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagGenerationPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Orchestrator implementing the "Multiple Dispatch Pattern" for dynamic DAG generation.
 * Handles the macro-loop with autonomous error correction via ReviewerAgent.
 */
@Slf4j
@Service
public class WorkflowOrchestrator {

    private final PlannerAgent plannerAgent;
    private final ReviewerAgent reviewerAgent;
    private final DagParserUtil dagParserUtil;
    private final MasMemoryService memoryService;
    private final MasCheckpointService checkpointService;
    private final MasEventLogService eventLogService;
    private final ExecutorService masNodeExecutorPool;
    private final NodeRunner nodeRunner;
    private final NodeRunner reactNodeRunner;
    private final ObjectMapper objectMapper;

    private static final int MAX_PHASES = 50;

    @Autowired
    public WorkflowOrchestrator(PlannerAgent plannerAgent,
                                 ReviewerAgent reviewerAgent,
                                 DagParserUtil dagParserUtil,
                                 MasMemoryService memoryService,
                                 MasCheckpointService checkpointService,
                                 MasEventLogService eventLogService,
                                 @Qualifier("masNodeExecutorPool") ExecutorService masNodeExecutorPool,
                                 NodeRunner nodeRunner,
                                 @Autowired(required = false) @Qualifier("reactNodeRunner") NodeRunner reactNodeRunner,
                                 ObjectMapper objectMapper) {
        this.plannerAgent = plannerAgent;
        this.reviewerAgent = reviewerAgent;
        this.dagParserUtil = dagParserUtil;
        this.memoryService = memoryService;
        this.checkpointService = checkpointService;
        this.eventLogService = eventLogService;
        this.masNodeExecutorPool = masNodeExecutorPool;
        this.nodeRunner = nodeRunner;
        this.reactNodeRunner = reactNodeRunner;
        this.objectMapper = objectMapper;
    }

    public MasOrchestrationResult executeMacroLoop(String sessionId, String userGoal) {
        log.info("[WorkflowOrchestrator] Starting macro-loop for Session: {}, Goal: {}", sessionId, userGoal);
        
        GlobalSessionMemory sessionMemory = memoryService.getSessionMemory(sessionId);
        sessionMemory.put("userGoal", userGoal);
        
        // Restore phaseCount from session (supports RESUME recovery)
        int phaseCount = 1;
        Object savedPhaseCount = sessionMemory.get("_phaseCount");
        if (savedPhaseCount != null) {
            try {
                phaseCount = Integer.parseInt(savedPhaseCount.toString());
                log.info("[WorkflowOrchestrator] Restored phaseCount={} from session", phaseCount);
            } catch (NumberFormatException e) {
                log.warn("[WorkflowOrchestrator] Invalid saved phaseCount: {}, resetting to 1", savedPhaseCount);
            }
        }

        while (true) {
            log.info("[WorkflowOrchestrator] ======== BEGIN PHASE {} ========", phaseCount);
            
            // Safety cap: Delegate to Reviewer for a final decision instead of crashing
            if (phaseCount > MAX_PHASES) {
                 log.warn("[WorkflowOrchestrator] Reached safety iteration limit ({}). Calling Reviewer for final verdict.", MAX_PHASES);
                 return reviewerAgent.review(new LocalNodeMemory("LIMIT-REVIEW-" + sessionId, sessionMemory), 
                     "Reached maximum phase limit of " + MAX_PHASES + ". Task seems stuck in a loop.");
            }

            // 1. Give Planner Agent the context to evaluate
            String plannerLoopId = "PLANNER-PHASE-" + phaseCount;
            LocalNodeMemory plannerLocalMem = new LocalNodeMemory(plannerLoopId, sessionMemory);
            
            String planJson = null;
            int retries = 0;
            while (retries < 3) {
                try {
                    planJson = plannerAgent.execute(plannerLocalMem);
                    break; 
                } catch (Exception e) {
                    retries++;
                    log.warn("[WorkflowOrchestrator] Planner execution attempt {} failed. Reason: {}", retries, e.getMessage());
                    if (retries >= 3) {
                        // All retries failed, let Reviewer handle or decide
                        log.error("[WorkflowOrchestrator] Planner Agent failed after 3 retries.");
                        return reviewerAgent.review(plannerLocalMem, "Planner execution failed repeatedly: " + e.getMessage());
                    }
                }
            }
            plannerLocalMem.mergeToGlobal();

            // 2. Parse and evaluate status
            DagGenerationPlan plan;
            try {
                plan = objectMapper.readValue(planJson, DagGenerationPlan.class);
            } catch (Exception e) {
                log.error("[WorkflowOrchestrator] Error parsing Planner JSON: {}", planJson, e);
                // Call Reviewer to "fix" or decide next step
                log.info("[WorkflowOrchestrator] Tasking Reviewer to handle parsing failure.");
                MasOrchestrationResult reviewResult = reviewerAgent.review(plannerLocalMem, 
                    "Failed to parse Planner JSON output. Raw output was: " + planJson + ". Error: " + e.getMessage());
                
                if (reviewResult.getType() == MasOrchestrationResult.ResultType.CONTINUE) {
                    log.info("[WorkflowOrchestrator] Reviewer suggested CONTINUE. Retrying loop...");
                    continue; 
                }
                return reviewResult;
            }

            if (plan.getStatus() == DagGenerationPlan.Status.DONE) {
                log.info("[WorkflowOrchestrator] Planner decided task is DONE. Reasoning: {}", plan.getReasoning());
                return MasOrchestrationResult.builder()
                        .type(MasOrchestrationResult.ResultType.FINISH)
                        .history(sessionMemory.get("globalHistory") != null ? sessionMemory.get("globalHistory").toString() : "")
                        .build();
            } else if (plan.getStatus() == DagGenerationPlan.Status.FAILED) {
                log.error("[WorkflowOrchestrator] Planner decided task has FAILED. Reasoning: {}", plan.getReasoning());
                return MasOrchestrationResult.builder()
                        .type(MasOrchestrationResult.ResultType.FAIL)
                        .errorMessage(plan.getReasoning())
                        .history(sessionMemory.get("globalHistory") != null ? sessionMemory.get("globalHistory").toString() : "")
                        .build();
            } else if (plan.getStatus() == DagGenerationPlan.Status.RESUME) {
                log.info("[WorkflowOrchestrator] Planner decided to RESUME later. Reason: {}, Next: {}", plan.getReasoning(), plan.getNextExecuteAt());
                LocalDateTime nextTime = null;
                if (plan.getNextExecuteAt() != null) {
                    try {
                        nextTime = LocalDateTime.parse(plan.getNextExecuteAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } catch (Exception e) {
                        log.warn("[WorkflowOrchestrator] Failed to parse nextExecuteAt: {}", plan.getNextExecuteAt());
                    }
                }
                return MasOrchestrationResult.builder()
                        .type(MasOrchestrationResult.ResultType.SUSPEND)
                        .nextExecuteTime(nextTime)
                        .history(sessionMemory.get("globalHistory") != null ? sessionMemory.get("globalHistory").toString() : "")
                        .build();
            }

            // 3. Status is IN_PROGRESS -> Generate and Dispatch DAG Engine
            List<GraphNode> microBatch = dagParserUtil.parse(plan);
            if (microBatch.isEmpty()) {
                log.warn("[WorkflowOrchestrator] Planner returned IN_PROGRESS but yielded no nodes.");
                // Ask reviewer why we have an empty batch if it's still in progress
                return reviewerAgent.review(plannerLocalMem, "Planner returned IN_PROGRESS but yielded an empty node list.");
            }

            log.info("[WorkflowOrchestrator] Phase {}: Produced {} nodes. Reasoning: {}", phaseCount, microBatch.size(), plan.getReasoning());
            DagExecutionEngine engine = new DagExecutionEngine(
                    sessionMemory, checkpointService, memoryService, eventLogService,
                    masNodeExecutorPool, nodeRunner, reactNodeRunner);
            
            for (GraphNode node : microBatch) {
                engine.addNode(node);
                plan.getNodes().stream()
                    .filter(p -> p.getId().equals(node.getNodeId()))
                    .findFirst()
                    .ifPresent(p -> sessionMemory.put(node.getNodeId() + "_instruction", p.getInstruction()));
            }

            DagExecutionResult dagResult = engine.execute();
            if (!dagResult.isSuccess()) {
                log.warn("[WorkflowOrchestrator] Phase {} DAG execution not fully successful: outcome={}, failed={}, skipped={}",
                        phaseCount, dagResult.getOutcome(), dagResult.getFailedCount(), dagResult.getSkippedCount());
                // Let the Reviewer decide how to handle partial failures
                if (dagResult.getOutcome() == DagExecutionResult.Outcome.DEADLOCK
                        || dagResult.getOutcome() == DagExecutionResult.Outcome.TIMEOUT) {
                    return MasOrchestrationResult.builder()
                            .type(MasOrchestrationResult.ResultType.FAIL)
                            .errorMessage("DAG execution " + dagResult.getOutcome() + ": "
                                    + dagResult.getFailedCount() + " failed, " + dagResult.getSkippedCount() + " skipped")
                            .build();
                }
                // PARTIAL_FAILURE: continue to next phase so Planner can adapt
            }
            
            String history = sessionMemory.get("globalHistory") != null ? sessionMemory.get("globalHistory").toString() : "";
            StringBuilder phaseReport = new StringBuilder();
            phaseReport.append("\n- Phase ").append(phaseCount)
                    .append(": ").append(microBatch.size()).append(" nodes")
                    .append(", outcome=").append(dagResult.getOutcome())
                    .append(", success=").append(dagResult.getSuccessCount())
                    .append(", failed=").append(dagResult.getFailedCount())
                    .append(", skipped=").append(dagResult.getSkippedCount())
                    .append(", duration=").append(dagResult.getDurationMs()).append("ms");
            if (!dagResult.getFailures().isEmpty()) {
                phaseReport.append("\n  Failures:");
                for (DagExecutionResult.NodeFailureDetail f : dagResult.getFailures()) {
                    phaseReport.append("\n    - [").append(f.getNodeId()).append("/").append(f.getAgentRole())
                            .append("]: ").append(f.getErrorMessage());
                }
            }
            history += phaseReport.toString();
            sessionMemory.put("globalHistory", history);

            log.info("[WorkflowOrchestrator] ======== END PHASE {} ========\n", phaseCount);
            phaseCount++;
            // Persist phaseCount for RESUME recovery
            sessionMemory.put("_phaseCount", phaseCount);
        }
    }
}
