package com.hzltd.module.adv.model;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 广告计划 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCampaignModel {

    private String externalId;
    private String name;
    private String campaignType;
    private String targetType;
    private String objective;
    private String status;
    private String budgetType;
    private BigDecimal budget;
    private BigDecimal totalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    /**
     * 广告平台 (AMAZON, GOOGLE, etc.)
     */
    private String platform;
    /**
     * 扩展属性 (Map 格式)
     */
    private Map<String, Object> attributes;
    private Object extData; // JSON or Object

    public AdsCampaignModel addAttribute(String key, Object attr) {
        if (attributes == null) {
            attributes = Maps.newHashMap();
        }
        attributes.put(key, attr);
        return this;
    }

}
