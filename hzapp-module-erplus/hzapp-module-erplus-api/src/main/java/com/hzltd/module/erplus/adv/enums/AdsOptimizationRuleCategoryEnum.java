package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告优化规则分类枚举
 */
@Getter
@AllArgsConstructor
public enum AdsOptimizationRuleCategoryEnum {

    BID("BID", "竞价");

    private final String category;
    private final String name;

}
