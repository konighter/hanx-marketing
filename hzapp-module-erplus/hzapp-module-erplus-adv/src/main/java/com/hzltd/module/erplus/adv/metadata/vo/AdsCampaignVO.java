package com.hzltd.module.erplus.adv.metadata.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 广告计划 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCampaignVO {

    private String externalId;
    private String name;
    private String campaignType;
    private String objective;
    private String status;
    private String budgetType;
    private BigDecimal dailyBudget;
    private BigDecimal totalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
//    private String biddingStrategy;
    private Object extData; // JSON or Object

}
