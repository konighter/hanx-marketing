package com.hzltd.module.erplus.adv.automation.engine;

import com.hzltd.module.erplus.adv.auto_plan.service.AdsAutomationPlanService;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import com.hzltd.module.erplus.system.api.ErpTaskEngine;
import com.hzltd.module.erplus.system.api.ErpTaskHandler;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 广告自动化计划执行处理器
 * 负责扫描广告报表并执行调价、拓词等操作
 *
 * @author antigravity
 */
@Slf4j
@Component
public class AdsAutomationPlanTaskHandler implements ErpTaskHandler {

    public static final String TASK_TYPE = "ADV_AUTO_PLAN_EXEC";

    @Resource
    private AdsAutomationPlanService adsAutomationPlanService;

    @Resource
    private ErpTaskEngine erpTaskEngine;

    @Resource
    private AdsActionDispatcher adsActionDispatcher;

    @Override
    public ErpTaskResult onStart(ErpTaskDTO task) {
        return onPoll(task);
    }

    @Override
    public ErpTaskResult onPoll(ErpTaskDTO task) {
        Long planId = getPlanId(task);
        if (planId == null) {
            return ErpTaskResult.failed("Task context missing planId");
        }

        AdsAutomationPlanDO plan = adsAutomationPlanService.getPlanDO(planId);
        if (plan == null) {
            return ErpTaskResult.failed("Automation plan not found: " + planId);
        }

        // 1. 检查计划状态，如果不是 RUNNING 则停止执行
        if (!"RUNNING".equals(plan.getStatus())) {
            log.info("Automation plan {} is not running, current status: {}", planId, plan.getStatus());
            return ErpTaskResult.success();
        }

        try {
            log.info("Starting execution for automation plan: {} ({})", plan.getName(), planId);
            
            // TODO: Step 1. 获取广告报表数据 (Search Term Report)
            
            // TODO: Step 2. 匹配当前 SKU 相关的指标
            
            // TODO: Step 3. 决策分类 (WIN / TEST / KILL)
            
            // TODO: Step 4. 分发原子操作
            adsActionDispatcher.executeDummy();

            // 2. 提交下一次执行任务 (24小时后)
            scheduleNextRun(plan, task);

            return ErpTaskResult.success();
        } catch (Exception e) {
            log.error("Failed to execute automation plan: " + planId, e);
            return ErpTaskResult.failed(e.getMessage());
        }
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    private Long getPlanId(ErpTaskDTO task) {
        Object planId = task.getContext().get("planId");
        if (planId instanceof Number) {
            return ((Number) planId).longValue();
        } else if (planId instanceof String) {
            return Long.valueOf((String) planId);
        }
        return null;
    }

    private void scheduleNextRun(AdsAutomationPlanDO plan, ErpTaskDTO currentTask) {
        // 调度 24 小时后的下一次任务
        long nextScheduledAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000L);
        
        erpTaskEngine.submitTask(ErpTaskSubmitRequest.builder()
                .shopId(plan.getShopId())
                .platform(plan.getPlatform())
                .taskType(TASK_TYPE)
                .taskUniqueId("AUTO_PLAN_" + plan.getId() + "_" + (System.currentTimeMillis() / 1000))
                .context(Map.of("planId", plan.getId()))
                .scheduledAt(nextScheduledAt)
                .build());
        
        log.info("Scheduled next run for plan {} at epoch {}", plan.getId(), nextScheduledAt);
    }
}
