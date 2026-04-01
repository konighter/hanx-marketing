package com.hzltd.module.adv.model;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 广告素材/广告实体 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdModel {

    private String externalId;
    private String adGroupExternalId;
    private String name;
    private String adFormat;
    private String status;
    private String headline;
    private String description;
    private String landingPageUrl;
    private String callToAction;
    private String reviewStatus;
    private String asin;
    private String sku;
    /**
     * 广告平台
     */
    private String platform;
    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;
    private Object extData; // JSON or Object

    public AdsAdModel addAttribute(String key, Object attr) {
        if (attributes == null) {
            attributes = Maps.newHashMap();
        }
        attributes.put(key, attr);
        return this;
    }
}
