package com.hzltd.module.erplus.ai.mas.orchestration;


import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.ai.mas.agent.PlannerAgent;
import com.hzltd.module.erplus.ai.mas.agent.ReviewerAgent;
import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.execution.DagExecutionEngine;
import com.hzltd.module.erplus.ai.mas.execution.DagExecutionResult;
import com.hzltd.module.erplus.ai.mas.execution.GraphNode;
import com.hzltd.module.erplus.ai.mas.execution.NodeExecutorFactory;
import com.hzltd.module.erplus.ai.mas.memory.LocalNodeMemory;
import com.hzltd.module.erplus.ai.mas.persistence.MasCheckpointService;
import com.hzltd.module.erplus.ai.mas.prompt.schema.DagGenerationPlan;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasSessionMemory;

import static com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryKeys.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Orchestrates the overall multi-phase workflow by looping between Planner,
 * DAG Execution Engine, and Reviewer.
 */
@Slf4j
public class WorkflowOrchestrator {

    private final PlannerAgent plannerAgent;
    private final ReviewerAgent reviewerAgent;
    private final DagParserUtil dagParserUtil;
    private final MasMemoryManager memoryManager;
    private final MasCheckpointService checkpointService;
    private final MasEventLogService eventLogService;
    private final ExecutorService masNodeExecutorPool;
    private final NodeExecutorFactory nodeExecutorFactory;

    private static final int MAX_PHASES = 50;

    public WorkflowOrchestrator(PlannerAgent plannerAgent,
                                 ReviewerAgent reviewerAgent,
                                 DagParserUtil dagParserUtil,
                                 MasMemoryManager memoryManager,
                                 MasCheckpointService checkpointService,
                                 MasEventLogService eventLogService,
                                 ExecutorService masNodeExecutorPool,
                                 NodeExecutorFactory nodeExecutorFactory) {
        this.plannerAgent = plannerAgent;
        this.reviewerAgent = reviewerAgent;
        this.dagParserUtil = dagParserUtil;
        this.memoryManager = memoryManager;
        this.checkpointService = checkpointService;
        this.eventLogService = eventLogService;
        this.masNodeExecutorPool = masNodeExecutorPool;
        this.nodeExecutorFactory = nodeExecutorFactory;
    }

    /**
     * Executes the macro reasoning loop for a given goal.
     */
    public MasOrchestrationResult executeMacroLoop(String sessionId, String goal) {
        log.info("[WorkflowOrchestrator] Starting macro loop for session: {} goal: {}", sessionId, goal);
        
        MasSessionMemory sessionMemory = memoryManager.getSessionMemory(sessionId);
        if (sessionMemory.get(USER_GOAL) == null) {
            sessionMemory.put(USER_GOAL, goal);
        }

        int phaseCount = 1;
        Object savedPhaseCount = sessionMemory.get(PHASE_COUNT);
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
                        log.error("[WorkflowOrchestrator] Planner Agent failed after 3 retries.");
                        return reviewerAgent.review(plannerLocalMem, "Planner execution failed repeatedly: " + e.getMessage());
                    }
                }
            }
            plannerLocalMem.mergeToGlobal();

            // 2. Parse and evaluate status
            DagGenerationPlan plan;
            try {
                plan = JsonUtils.parseObject(planJson, DagGenerationPlan.class);
            } catch (Exception e) {
                log.error("[WorkflowOrchestrator] Error parsing Planner JSON: {}", planJson, e);
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
                log.info("[WorkflowOrchestrator] Planner returned DONE. Reasoning: {}", plan.getReasoning());
                return MasOrchestrationResult.builder()
                        .type(MasOrchestrationResult.ResultType.FINISH)
                        .output(plan.getReasoning())
                        .history(sessionMemory.get(GLOBAL_HISTORY) != null ? sessionMemory.get(GLOBAL_HISTORY).toString() : "")
                        .build();
            }

            if (plan.getStatus() == DagGenerationPlan.Status.SUSPEND) {
                log.info("[WorkflowOrchestrator] Planner returned SUSPEND. Next Execute At: {}", plan.getNextExecuteAt());
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
                        .history(sessionMemory.get(GLOBAL_HISTORY) != null ? sessionMemory.get(GLOBAL_HISTORY).toString() : "")
                        .build();
            }

            // 3. Status is IN_PROGRESS -> Generate and Dispatch DAG Engine
            List<GraphNode> microBatch = dagParserUtil.parse(plan);
            if (microBatch.isEmpty()) {
                log.warn("[WorkflowOrchestrator] Planner returned IN_PROGRESS but yielded no nodes.");
                return reviewerAgent.review(plannerLocalMem, "Planner returned IN_PROGRESS but yielded an empty node list.");
            }

            log.info("[WorkflowOrchestrator] Phase {}: Produced {} nodes. Reasoning: {}", phaseCount, microBatch.size(), plan.getReasoning());
            DagExecutionEngine engine = new DagExecutionEngine(
                    sessionMemory, checkpointService, memoryManager, eventLogService,
                    masNodeExecutorPool, nodeExecutorFactory);
            
            for (GraphNode node : microBatch) {
                engine.addNode(node);
            }

            DagExecutionResult engineResult = engine.execute();
            log.info("[WorkflowOrchestrator] Phase {} execution finished. Results: {}", phaseCount, engineResult);

            // 4. Update phase counter and context
            phaseCount++;
            sessionMemory.put(PHASE_COUNT, phaseCount);
            
            // Incrementally append to history for human readability if needed
            sessionMemory.put(GLOBAL_HISTORY, (sessionMemory.get(GLOBAL_HISTORY) != null ? sessionMemory.get(GLOBAL_HISTORY) : "") 
                    + "\n--- Phase Result ---\n" + engineResult.toString());
            
            memoryManager.saveToDb(sessionId);

            // Loop continues until Planner says DONE or Reviewer breaks it.
        }
    }
}
