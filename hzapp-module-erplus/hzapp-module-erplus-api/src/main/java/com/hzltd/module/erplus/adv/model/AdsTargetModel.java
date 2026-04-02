package com.hzltd.module.erplus.adv.model;

import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
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
public class AdsTargetModel {

    private String externalId;
    private String adEntityId;
    private AdsEntityTypeEnum adEntityType;

    /**
     * 定向类型,
     * 亚马逊: 关键词 & 品牌 & 商品
     */
    private String targetType;
    /**
     * 匹配类型
     * 亚马逊:
     *  关键词: BROAD/EXACT/PHRASE
     *  品牌&关键词: SponsoredProductsCreateTargetingExpressionPredicateType
     */
    private String matchType;

    private String matchValue;

    /**
     * 定向出价, 否定定向没有出价
     */
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
