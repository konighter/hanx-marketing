package com.hzltd.module.erplus.adv.metadata.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 广告关键词 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsKeywordVO {

    private String externalId;
    private String adGroupExternalId;
    private String keywordText;
    private String matchType;
    private BigDecimal bid;
    private String status;
    private Boolean isNegative;
    private String extData; // JSON string

}
