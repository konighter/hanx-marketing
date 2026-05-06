package com.hzltd.module.erplus.adv.auto_plan.engine;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.auto_plan.domain.AdsAction;
import com.hzltd.module.erplus.adv.auto_plan.domain.AdsRule;
import com.hzltd.module.erplus.adv.auto_plan.domain.AdsStrategyBlueprint;
import com.hzltd.module.erplus.adv.auto_plan.service.AdsAutomationLogService;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationTemplateDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 广告自动化核心引擎
 */
@Slf4j
@Component
public class AdsAutomationEngine {

    @Resource
    private AdsRuleEvaluator evaluator;

    @Resource
    private AdsActionDispatcher dispatcher;

    @Resource
    private AdsAutomationLogService logService;

    /**
     * 执行自动化计划
     *
     * @param plan     自动化计划
     * @param template 关联的模版
     * @param dataList 待处理的数据列表 (如 Search Term 报表行)
     */
    public void execute(AdsAutomationPlanDO plan, AdsAutomationTemplateDO template, List<Map<String, Object>> dataList) {
        log.info("Starting automation plan execution: [id={}, name={}]", plan.getId(), template.getName());

        AdsStrategyBlueprint blueprint = parseBlueprint(template.getConfig());
        if (blueprint == null || blueprint.getRules() == null) {
            log.warn("Empty blueprint for template: {}", template.getId());
            return;
        }

        Map<String, Object> context = parseContext(plan.getContext());

        for (Map<String, Object> metrics : dataList) {
            for (AdsRule rule : blueprint.getRules()) {
                List<AdsAction> actions = evaluator.evaluate(rule, metrics, context);
                for (AdsAction action : actions) {
                    log.info("Rule triggered: {} for data row: {}", rule.getName(), metrics);
                    dispatcher.dispatch(plan.getPlatform(), action, context);
                    logService.recordLog(plan.getId(), rule.getName(), metrics, action);
                }
            }
        }
    }

    private AdsStrategyBlueprint parseBlueprint(Object blueprint) {
        if (blueprint == null) return null;
        if (blueprint instanceof String) {
            return JsonUtils.parseObject((String) blueprint, AdsStrategyBlueprint.class);
        }
        return JsonUtils.parseObject(JsonUtils.toJsonString(blueprint), AdsStrategyBlueprint.class);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseContext(Object context) {
        if (context == null) return Map.of();
        if (context instanceof String) {
            return JsonUtils.parseObject((String) context, Map.class);
        }
        return JsonUtils.parseObject(JsonUtils.toJsonString(context), Map.class);
    }
}
