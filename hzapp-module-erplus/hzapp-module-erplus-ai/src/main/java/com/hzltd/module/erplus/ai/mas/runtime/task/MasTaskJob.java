package com.hzltd.module.erplus.ai.mas.runtime.task;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.MasOrchestrationResult;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.WorkflowOrchestrator;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskStatusEnum;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskTypeEnum;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryKeys;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.task.util.TaskContextFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MAS 业务任务定时调度 Job
 * 负责扫描 PENDING 状态的任务并分发执行
 */
@Slf4j
@Component
public class MasTaskJob {

    private final MasTaskService taskService;
    private final WorkflowOrchestrator workflowOrchestrator;
    private final MasMemoryManager memoryManager;

    public MasTaskJob(MasTaskService taskService, WorkflowOrchestrator workflowOrchestrator, MasMemoryManager memoryManager) {
        this.taskService = taskService;
        this.workflowOrchestrator = workflowOrchestrator;
        this.memoryManager = memoryManager;
    }

    /**
     * 每 5 秒扫描一次待处理任务
     * 实际生产环境建议使用分布式 Job 框架（如 XXL-JOB）
     */
//    @Scheduled(fixedDelay = 5000)
    public void scheduleTasks() {
        List<MasTaskDO> pendingTasks = taskService.getPendingTasks();
        if (pendingTasks.isEmpty()) return;

        log.info("[MasTaskJob] Found {} pending tasks to process", pendingTasks.size());

        for (MasTaskDO task : pendingTasks) {
            processTask(task);
        }
    }

    private void processTask(MasTaskDO task) {
        try {
            if (MasTaskTypeEnum.LEAF.getType().equals(task.getTaskType())) {
                // Leaf task -> Execute the actual AI logic
                // 1. Move to RUNNING
                taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.RUNNING.getStatus(), null);

                log.info("[MasTaskJob] Executing LEAF task: {}", task.getName());

                // 1. Build standardized SessionId
                String standardizedSessionId = task.getSessionId() + "_Task#" + task.getId();

                // Optional: Pre-hydrate memory from task history
                hydrateHistoryIfEmpty(standardizedSessionId, task);

                executeLeafTask(standardizedSessionId, task);
            } else {
                // Container task (SEQUENTIAL/PARALLEL)
                // 1. Move to RUNNING
                taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.RUNNING.getStatus(), null);

                log.info("[MasTaskJob] Container task {} is now RUNNING. Activating sub-tasks.", task.getName());
                taskService.activateChildren(task.getId());
            }
        } catch (Exception e) {
            log.error("[MasTaskJob] Failed to process task {}", task.getId(), e);
            taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.FAILED.getStatus(), e.getMessage());
        }
    }

    private void executeLeafTask(String sessionId, MasTaskDO task) {
        // Execute asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                log.info("[MasTaskJob] Triggering WorkflowOrchestrator for task: {}", task.getId());
                
                // 2. Build rich prompt goal: Strategy + Retry + Input
                String formattedGoal = TaskContextFormatter.formatTaskGoal(task);
                
                MasOrchestrationResult result = workflowOrchestrator.executeMacroLoop(sessionId, formattedGoal);
                
                if (result.getType() == MasOrchestrationResult.ResultType.SUSPEND) {
                    log.info("[MasTaskJob] Task {} matches SUSPEND. Moving to SUSPEND, nextExecuteAt={}", task.getId(), result.getNextExecuteTime());
                    taskService.suspendTask(task.getId(), result.getNextExecuteTime(), result.getHistory());
                } else if (result.getType() == MasOrchestrationResult.ResultType.FAIL) {
                    log.error("[MasTaskJob] Task {} matches FAIL. Reason: {}", task.getId(), result.getErrorMessage());
                    // Use updateTaskStatus with outputData as error details
                    taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.FAILED.getStatus(), result.getErrorMessage());
                } else {
                    log.info("[MasTaskJob] Task {} matches FINISH. Marking SUCCESS.", task.getId());
                    // Success: Global History is persisted into outputData for audit/visibility
                    taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.SUCCESS.getStatus(), result.getHistory());
                }
                
                log.info("[MasTaskJob] Leaf task {} finished with result type: {}", task.getId(), result.getType());
            } catch (Exception e) {
                log.error("[MasTaskJob] Error during Leaf execution for task {}", task.getId(), e);
                taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.FAILED.getStatus(), e.getMessage());
            }
        });
    }

    private void hydrateHistoryIfEmpty(String sessionId, MasTaskDO task) {
        if (task.getOutputData() == null || task.getOutputData().isEmpty()) return;

        MasSessionMemory sessionMemory = memoryManager.getSessionMemory(sessionId);
        if (sessionMemory.get(MasMemoryKeys.GLOBAL_HISTORY) == null) {
            log.info("[MasTaskJob] Hydrating GLOBAL_HISTORY for session {} from task history record", task.getSessionId());
            sessionMemory.put(MasMemoryKeys.GLOBAL_HISTORY, task.getOutputData());
            
            // Also try to recover phase count if it looks sequential
            if (task.getRetryData() != null && task.getRetryData().contains("PHASE")) {
                // This is optional and heuristic-based for now
                log.debug("[MasTaskJob] Attempting to keep phase count from memory if it exists");
            }
        }
    }
}
