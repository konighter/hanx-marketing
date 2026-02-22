package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 亚马逊广告区域枚举
 * 参照：https://advertising.amazon.com/API/docs/en-us/info/api-overview#api-endpoints
 */
@Getter
@AllArgsConstructor
public enum AdsAmazonRegionEnum {

    NA("North America", "advertising-api.amazon.com"),
    EU("Europe", "advertising-api-eu.amazon.com"),
    FE("Far East", "advertising-api-fe.amazon.com");

    /**
     * 区域名称
     */
    private final String name;
    /**
     * API 域名
     */
    private final String endpoint;

}
