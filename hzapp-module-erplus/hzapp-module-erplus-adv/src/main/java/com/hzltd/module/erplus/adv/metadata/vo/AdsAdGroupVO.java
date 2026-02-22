package com.hzltd.module.erplus.adv.metadata.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 广告组 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdGroupVO {

    private String externalId;
    private String campaignExternalId;
    private String name;
    private String status;
    private BigDecimal defaultBid;
    private String bidStrategy;
    private String targetingType;
    private String extData; // JSON string

}
