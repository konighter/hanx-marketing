package com.hzltd.module.erplus.adv.metadata.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

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
    /**
     * 广告平台
     */
    private String platform;
    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;
    private Object extData; // JSON or Object

}
