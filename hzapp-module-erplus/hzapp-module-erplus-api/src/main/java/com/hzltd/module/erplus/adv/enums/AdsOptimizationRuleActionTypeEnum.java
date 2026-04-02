package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告优化规则动作类型枚举
 */
@Getter
@AllArgsConstructor
public enum AdsOptimizationRuleActionTypeEnum {

    ADOPT("ADOPT", "采纳");

    private final String type;
    private final String name;

}
