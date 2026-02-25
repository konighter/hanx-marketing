package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告优化规则重复类型枚举
 */
@Getter
@AllArgsConstructor
public enum AdsOptimizationRuleRecurrenceTypeEnum {

    DAILY("DAILY", "每日"),
    WEEKLY("WEEKLY", "每周");

    private final String type;
    private final String name;

}
