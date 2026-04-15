package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告优化规则动作操作符枚举
 */
@Getter
@AllArgsConstructor
public enum AdsOptimizationRuleOperatorEnum {

    INCREMENT("INCREMENT", "增加"),
    DECREMENT("DECREMENT", "减少");

    private final String operator;
    private final String name;

}
