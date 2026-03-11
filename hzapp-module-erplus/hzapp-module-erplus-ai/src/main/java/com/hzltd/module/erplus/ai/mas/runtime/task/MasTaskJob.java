package com.hzltd.module.erplus.ai.mas.runtime.task;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.MasOrchestrationResult;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.WorkflowOrchestrator;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskStatusEnum;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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

    public MasTaskJob(MasTaskService taskService, WorkflowOrchestrator workflowOrchestrator) {
        this.taskService = taskService;
        this.workflowOrchestrator = workflowOrchestrator;
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
            // 1. Move to RUNNING (Optimistic lock should be added in a real distributed env)
            taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.RUNNING.getStatus(), null);

            if (MasTaskTypeEnum.LEAF.getType().equals(task.getTaskType())) {
                // Leaf task -> Execute the actual AI logic
                log.info("[MasTaskJob] Executing LEAF task: {}", task.getName());
                executeLeafTask(task);
            } else {
                // Container task (SEQUENTIAL/PARALLEL)
                // In our design, children are created upfront. 
                // Container status enters RUNNING and its job is to wait/orchestrate.
                log.info("[MasTaskJob] Container task {} is now RUNNING", task.getName());
            }
        } catch (Exception e) {
            log.error("[MasTaskJob] Failed to process task {}", task.getId(), e);
            taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.FAILED.getStatus(), e.getMessage());
        }
    }

    private void executeLeafTask(MasTaskDO task) {
        // Execute asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                log.info("[MasTaskJob] Triggering WorkflowOrchestrator for task: {}", task.getId());
                MasOrchestrationResult result = workflowOrchestrator.executeMacroLoop(task.getSessionId(), task.getInputData());
                
                if (result.getType() == MasOrchestrationResult.ResultType.SUSPEND) {
                    taskService.resumeTask(task.getId(), result.getNextExecuteTime(), result.getHistory());
                } else if (result.getType() == MasOrchestrationResult.ResultType.FAIL) {
                    taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.FAILED.getStatus(), result.getErrorMessage());
                } else {
                    // Default to SUCCESS for FINISH
                    taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.SUCCESS.getStatus(), result.getHistory());
                }
                
                log.info("[MasTaskJob] Leaf task {} finished with result type: {}", task.getId(), result.getType());
            } catch (Exception e) {
                log.error("[MasTaskJob] Error during Leaf execution for task {}", task.getId(), e);
                taskService.updateTaskStatus(task.getId(), MasTaskStatusEnum.FAILED.getStatus(), e.getMessage());
            }
        });
    }
}
