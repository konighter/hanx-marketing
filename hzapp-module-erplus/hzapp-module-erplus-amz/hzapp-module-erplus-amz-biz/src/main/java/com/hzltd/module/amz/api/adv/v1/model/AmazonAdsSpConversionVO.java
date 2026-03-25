package com.hzltd.module.amz.api.adv.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Amazon Marketing Stream - Sponsored Products Conversion Dataset VO
 * See: https://advertising.amazon.com/API/docs/en-us/guides/amazon-marketing-stream/datasets/sp-performance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonAdsSpConversionVO {

    /** 幂等令牌，用于去重 */
    private String idempotencyId;

    /** 数据集 ID (sp-conversion) */
    private String datasetId;

    /** 站点 ID */
    private String marketplaceId;

    /** 货币单位 */
    private String currency;

    /** 广告主 ID */
    private String advertiserId;

    /** 广告活动 ID */
    private String campaignId;

    /** 广告组 ID */
    private String adGroupId;

    /** 广告 ID */
    private String adId;

    /** 关键词 ID */
    private String keywordId;

    /** 时间窗口开始时间 (ISO 8601 date time) */
    private String timeWindowStart;

    // --- 销售额 (Sales) ---

    /** 1天点击归因销售额 */
    private BigDecimal attributedSales1d;
    /** 1天点击归因相同 SKU 销售额 */
    private BigDecimal attributedSales1dSameSku;

    /** 7天点击归因销售额 */
    private BigDecimal attributedSales7d;
    /** 7天点击归因相同 SKU 销售额 */
    private BigDecimal attributedSales7dSameSku;

    /** 14天点击归因销售额 */
    private BigDecimal attributedSales14d;
    /** 14天点击归因相同 SKU 销售额 */
    private BigDecimal attributedSales14dSameSku;

    /** 30天点击归因销售额 */
    private BigDecimal attributedSales30d;
    /** 30天点击归因相同 SKU 销售额 */
    private BigDecimal attributedSales30dSameSku;

    // --- 订单量 (Units Ordered) ---

    /** 1天点击归因订单量 */
    private Long attributedUnitsOrdered1d;
    /** 1天点击归因相同 SKU 订单量 */
    private Long attributedUnitsOrdered1dSameSku;

    /** 7天点击归因订单量 */
    private Long attributedUnitsOrdered7d;
    /** 7天点击归因相同 SKU 订单量 */
    private Long attributedUnitsOrdered7dSameSku;

    /** 14天点击归因订单量 */
    private Long attributedUnitsOrdered14d;
    /** 14天点击归因相同 SKU 订单量 */
    private Long attributedUnitsOrdered14dSameSku;

    /** 30天点击归因订单量 */
    private Long attributedUnitsOrdered30d;
    /** 30天点击归因相同 SKU 订单量 */
    private Long attributedUnitsOrdered30dSameSku;

}
