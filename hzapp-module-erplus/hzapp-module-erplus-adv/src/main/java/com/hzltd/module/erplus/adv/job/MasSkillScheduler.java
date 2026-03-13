package com.hzltd.module.erplus.adv.job;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.mas.runtime.task.MasTaskService;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.WorkflowOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MAS 技能长周期调度器
 * 定时扫描处于 RESUME / PENDING 状态的主任务并唤醒执行
 */
@Slf4j
@Component
public class MasSkillScheduler {

    private final MasTaskService masTaskService;
    private final WorkflowOrchestrator workflowOrchestrator;
    
    // 使用独立线程池异步丢给 WorkflowOrchestrator 执行，避免阻塞调度器线程
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public MasSkillScheduler(MasTaskService masTaskService, WorkflowOrchestrator workflowOrchestrator) {
        this.masTaskService = masTaskService;
        this.workflowOrchestrator = workflowOrchestrator;
    }

    /**
     * 每 1 分钟扫描一遍需要执行的主任务
     */
//    @Scheduled(cron = "0 * * * * ?")
    public void executeTaskScanner() {
        log.info("[MasSkillScheduler] 启动例行扫描：检查是否有满足执行条件的 MAS 技能任务...");
        try {
            List<MasTaskDO> pendingTasks = masTaskService.getPendingTasks();
            if (pendingTasks == null || pendingTasks.isEmpty()) {
                return;
            }

            for (MasTaskDO task : pendingTasks) {
                // 安全限制：我们只取宏观循环入口的顶级任务丢入 Orchestrator，
                // 嵌套层级的子任务应由引擎内部流转分配。
                if (task.getParentId() != null) {
                    continue; 
                }
                
                log.info("[MasSkillScheduler] => 发现挂起的主任务 (ID: {}, Type: {}, Status: {})，开始丢入执行池...", 
                        task.getId(), task.getTaskType(), task.getStatus());
                
                executorService.submit(() -> {
                    try {
                        String userGoal = "Auto Resume Execution";
                        workflowOrchestrator.executeMacroLoop(task.getId().toString(), userGoal);
                    } catch (Exception e) {
                        log.error("[MasSkillScheduler] 异步执行任务 {} 失败: ", task.getId(), e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("[MasSkillScheduler] 调度扫描异常", e);
        }
    }
}
