package com.hzltd.module.erplus.adv.automation.engine;

import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.erplus.adv.automation.service.AdsAutomationPlanService;
import com.hzltd.module.erplus.adv.automation.service.AdsAutomationPlanTaskFlowService;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import com.hzltd.module.erplus.adv.enums.AdsAutomationPlanStatusEnum;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.enums.ErpTaskResultStatusEnum;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.service.ErpTaskHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    private AdsAutomationPlanTaskFlowService taskFlowService;

    @Resource
    private ErpTaskEngine erpTaskEngine;

    @Resource
    private AdsActionDispatcher adsActionDispatcher;

    @Override
    public ErpTaskResult onStart(ErpTaskDTO task) {
        AdsAutomationPlanDO plan = getPlan(task);
        if (plan != null) {
            TenantUtils.execute(plan.getTenantId(), () -> {
                taskFlowService.initAdStructure(plan.getId());
            });
        }
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.SUBMITTED.getStatus())
                .build();
    }

    @Override
    public ErpTaskResult onPoll(ErpTaskDTO task) {
        AdsAutomationPlanDO plan = getPlan(task);
        if (plan == null) {
            return ErpTaskResult.failed("Task context missing planId");
        }

       return TenantUtils.execute(plan.getTenantId(), () -> {
            Long planId = plan.getId();

            // 1. 检查计划状态，如果不是 RUNNING 则停止执行
            if (!AdsAutomationPlanStatusEnum.RUNNING.getStatus().equals(plan.getStatus())) {
                log.info("Automation plan {} is not running, current status: {}", planId, plan.getStatus());
                return ErpTaskResult.success();
            }

            try {
                log.info("Starting execution for automation plan: {} ({})", plan.getName(), planId);

                // 执行自动化循环逻辑
                taskFlowService.executeAutomationCycle(planId);

                // 2. 提交下一次执行任务 (12小时后)
                return ErpTaskResult.submitted(null).setNextScheduleTime(System.currentTimeMillis() + (12 * 60 * 60 * 1000L));
            } catch (Exception e) {
                log.error("Failed to execute automation plan: " + planId, e);
                return ErpTaskResult.failed(e.getMessage());
            }
        });
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    private AdsAutomationPlanDO getPlan(ErpTaskDTO task) {
        Object planIdString = task.getContext().get("planId");
        Long planId;
        if (planIdString instanceof Number) {
            planId = ((Number) planIdString).longValue();
        } else if (planIdString instanceof String) {
            planId = Long.valueOf((String) planIdString);
        } else {
            planId = null;
        }

        if (planId == null) {
            return null;
        }

        AdsAutomationPlanDO plan = adsAutomationPlanService.getPlanDOWithoutTenant(planId);
        return plan;
    }

}
