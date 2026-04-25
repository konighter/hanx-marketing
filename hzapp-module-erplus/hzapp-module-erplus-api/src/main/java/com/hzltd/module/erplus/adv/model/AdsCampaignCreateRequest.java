package com.hzltd.module.erplus.adv.model;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class AdsCampaignCreateRequest {
 
    private String name;
 
    private String campaignType;
 
    private String status;
 
    private String budgetType;
 
    private BigDecimal dailyBudget;
 
    private BigDecimal totalBudget;
 
    private LocalDate startDate;
 
    private LocalDate endDate;
 
    private String biddingStrategy;
 
    private Map<String, Object> attributes = Maps.newHashMap();
 
    public AdsCampaignCreateRequest add(String field, Object value) {
        attributes.put(field, value);
        return this;
    }
}
