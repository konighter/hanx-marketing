package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告优化规则动作单位枚举
 */
@Getter
@AllArgsConstructor
public enum AdsOptimizationRuleUnitEnum {

    PERCENT("PERCENT", "百分比"),
    CURRENCY("CURRENCY", "货币金额");

    private final String unit;
    private final String name;

}
