package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告实体类型枚举
 *
 * @author hzadd
 */
@Getter
@AllArgsConstructor
public enum AdsEntityTypeEnum {

    SHOP("SHOP", "店铺"),
    ACCOUNT("ACCOUNT", "广告账号"),
    CAMPAIGN("CAMPAIGN", "广告计划"),
    ADGROUP("ADGROUP", "广告组"),
    AD("AD", "广告"),
    KEYWORD("KEYWORD", "关键词"),
    TARGET("TARGET", "商品投放"),
    CREATIVE("CREATIVE", "创意素材");

    private final String code;
    private final String name;
}
