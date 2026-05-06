package com.hzltd.module.erplus.adv.automation.engine;

import com.hzltd.module.erplus.adv.automation.domain.AdsAction;
import com.hzltd.module.erplus.adv.automation.domain.AdsCondition;
import com.hzltd.module.erplus.adv.automation.domain.AdsRule;
import com.hzltd.module.erplus.adv.enums.automation.AdsActionTypeEnum;
import com.hzltd.module.erplus.adv.enums.automation.AdsMetricEnum;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdsRuleEvaluatorTest {

    private final AdsRuleEvaluator evaluator = new AdsRuleEvaluator();

    @Test
    void testEvaluate_WinRule_Matched() {
        // Setup Rule: Orders >= 2 AND CPA <= targetCpa
        AdsRule rule = new AdsRule();
        rule.setName("WIN_RULE");

        AdsCondition cond1 = new AdsCondition();
        cond1.setMetric(AdsMetricEnum.ORDERS);
        cond1.setOperator(">=");
        cond1.setValue(2);

        AdsCondition cond2 = new AdsCondition();
        cond2.setMetric(AdsMetricEnum.CPA);
        cond2.setOperator("<=");
        cond2.setValue("context.targetCpa");

        rule.setConditions(List.of(cond1, cond2));

        AdsAction action = new AdsAction();
        action.setType(AdsActionTypeEnum.CREATE_KEYWORD);
        rule.setActions(List.of(action));

        // Setup Data
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("orders", 3);
        metrics.put("cpa", 10.5);

        Map<String, Object> context = new HashMap<>();
        context.put("targetCpa", 15.0);

        // Evaluate
        List<AdsAction> results = evaluator.evaluate(rule, metrics, context);

        assertFalse(results.isEmpty());
        assertEquals(AdsActionTypeEnum.CREATE_KEYWORD, results.get(0).getType());
    }

    @Test
    void testEvaluate_WinRule_NotMatched() {
        AdsRule rule = new AdsRule();
        
        AdsCondition cond1 = new AdsCondition();
        cond1.setMetric(AdsMetricEnum.ORDERS);
        cond1.setOperator(">=");
        cond1.setValue(2);
        
        rule.setConditions(List.of(cond1));
        rule.setActions(List.of(new AdsAction()));

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("orders", 1); // Less than 2

        List<AdsAction> results = evaluator.evaluate(rule, metrics, Collections.emptyMap());

        assertTrue(results.isEmpty());
    }
}
