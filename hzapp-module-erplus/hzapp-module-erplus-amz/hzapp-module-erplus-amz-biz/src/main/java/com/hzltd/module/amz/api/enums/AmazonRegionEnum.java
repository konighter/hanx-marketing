package com.hzltd.module.amz.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 亚马逊广告区域枚举
 * 参照：https://advertising.amazon.com/API/docs/en-us/info/api-overview#api-endpoints
 */
@Getter
@AllArgsConstructor
public enum AmazonRegionEnum {

    NA("North America","https://sellingpartnerapi-na.amazon.com", "advertising-api.amazon.com", "https://www.amazon.com/ap/oa", "NA", List.of("US", "CA", "MX")),
    EU("Europe","", "advertising-api-eu.amazon.com","https://eu.account.amazon.com/ap/oa",  "EU", List.of("UK")),
    FE("Far East","", "advertising-api-fe.amazon.com","https://apac.account.amazon.com/ap/oa",  "FE", List.of("JP"));

    /**
     * 区域名称
     */
    private final String name;

    /**
     * spiApi 域名
     */
    private final String spApiEndpoint;
    /**
     * API 域名
     */
    private final String adsEndpoint;

    /**
     * auth域名
     */
    private final String authEndpoint;

    private final String regionCode;

    private final List<String> countryCodes;


    public static AmazonRegionEnum ofCountryCode(String countryCode) {
        for (AmazonRegionEnum e : AmazonRegionEnum.values()) {
            if (e.countryCodes.contains(countryCode)) {
                return e;
            }
        }
        return null;
    }


}
