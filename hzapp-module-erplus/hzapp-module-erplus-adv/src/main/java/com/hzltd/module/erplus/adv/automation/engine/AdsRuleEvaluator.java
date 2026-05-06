package com.hzltd.module.erplus.adv.automation.engine;

import com.hzltd.module.erplus.adv.automation.domain.AdsAction;
import com.hzltd.module.erplus.adv.automation.domain.AdsCondition;
import com.hzltd.module.erplus.adv.automation.domain.AdsRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自动化规则判定器
 */
@Slf4j
@Component
public class AdsRuleEvaluator {

    /**
     * 判定一条规则是否触发
     *
     * @param rule    策略规则
     * @param metrics 当前指标数据
     * @param context 运行上下文参数 (用于解析变量)
     * @return 触发的动作列表，如果不匹配则返回空列表
     */
    public List<AdsAction> evaluate(AdsRule rule, Map<String, Object> metrics, Map<String, Object> context) {
        List<AdsCondition> conditions = rule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return rule.getActions();
        }

        for (AdsCondition condition : conditions) {
            if (!isConditionMet(condition, metrics, context)) {
                return new ArrayList<>();
            }
        }

        return rule.getActions();
    }

    private boolean isConditionMet(AdsCondition condition, Map<String, Object> metrics, Map<String, Object> context) {
        Object actualValue = metrics.get(condition.getMetric().getCode());
        if (actualValue == null) {
            return false;
        }

        Object targetValue = resolveValue(condition.getValue(), context);
        return compare(actualValue, condition.getOperator(), targetValue);
    }

    private Object resolveValue(Object rawValue, Map<String, Object> context) {
        if (rawValue instanceof String strValue && strValue.startsWith("context.")) {
            String key = strValue.substring(8);
            return context.get(key);
        }
        return rawValue;
    }

    private boolean compare(Object actual, String operator, Object target) {
        BigDecimal a = toBigDecimal(actual);
        BigDecimal b = toBigDecimal(target);

        if (a == null || b == null) {
            return false;
        }

        return switch (operator) {
            case ">=" -> a.compareTo(b) >= 0;
            case "<=" -> a.compareTo(b) <= 0;
            case "==" -> a.compareTo(b) == 0;
            case ">" -> a.compareTo(b) > 0;
            case "<" -> a.compareTo(b) < 0;
            default -> {
                log.warn("Unknown operator: {}", operator);
                yield false;
            }
        };
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
