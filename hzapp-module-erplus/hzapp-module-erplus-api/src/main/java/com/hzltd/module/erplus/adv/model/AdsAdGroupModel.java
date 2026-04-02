package com.hzltd.module.erplus.adv.model;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 广告组 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdGroupModel {

    private String externalId;
    private String campaignExternalId;
    private String name;
    private String status;
    private BigDecimal defaultBid;
    private String bidStrategy;
    private String targetingType;
    /**
     * 广告平台
     */
    private String platform;
    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;
    private Object extData; // JSON or Object

    public AdsAdGroupModel addAttribute(String key, Object attr) {
        if (attributes == null) {
            attributes = Maps.newHashMap();
        }
        attributes.put(key, attr);
        return this;
    }
}
