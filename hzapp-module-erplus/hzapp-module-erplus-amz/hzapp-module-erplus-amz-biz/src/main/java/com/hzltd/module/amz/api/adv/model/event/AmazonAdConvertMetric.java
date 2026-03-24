package com.hzltd.module.amz.api.adv.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Amazon Advertising SP-API Conversion Metric Bean
 * 用于处理 sp-conversion 数据集
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonAdConvertMetric {

    @JsonProperty("idempotency_id")
    private String idempotencyId;

    @JsonProperty("dataset_id")
    private String datasetId;

    @JsonProperty("marketplace_id")
    private String marketplaceId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("advertiser_id")
    private String advertiserId;

    @JsonProperty("campaign_id")
    private String campaignId;

    @JsonProperty("ad_group_id")
    private String adGroupId;

    @JsonProperty("ad_id")
    private String adId;

    @JsonProperty("keyword_id")
    private String keywordId;

    @JsonProperty("placement")
    private String placement;

    /**
     * 时间窗口开始时间（Unix 秒时间戳，由 ISO 8601 字符串转换）
     */
    @JsonProperty("time_window_start")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AmazonZonedDateTimeDeserializer.class)
    private Long timeWindowStart;

    // ---- 归因转化次数 ----

    @JsonProperty("attributed_conversions_1d")
    private Long attributedConversions1d;

    @JsonProperty("attributed_conversions_7d")
    private Long attributedConversions7d;

    @JsonProperty("attributed_conversions_14d")
    private Long attributedConversions14d;

    @JsonProperty("attributed_conversions_30d")
    private Long attributedConversions30d;

    @JsonProperty("attributed_conversions_1d_same_sku")
    private Long attributedConversions1dSameSku;

    @JsonProperty("attributed_conversions_7d_same_sku")
    private Long attributedConversions7dSameSku;

    @JsonProperty("attributed_conversions_14d_same_sku")
    private Long attributedConversions14dSameSku;

    @JsonProperty("attributed_conversions_30d_same_sku")
    private Long attributedConversions30dSameSku;

    // ---- 归因销售额 ----

    @JsonProperty("attributed_sales_1d")
    private Double attributedSales1d;

    @JsonProperty("attributed_sales_7d")
    private Double attributedSales7d;

    @JsonProperty("attributed_sales_14d")
    private Double attributedSales14d;

    @JsonProperty("attributed_sales_30d")
    private Double attributedSales30d;

    @JsonProperty("attributed_sales_1d_same_sku")
    private Double attributedSales1dSameSku;

    @JsonProperty("attributed_sales_7d_same_sku")
    private Double attributedSales7dSameSku;

    @JsonProperty("attributed_sales_14d_same_sku")
    private Double attributedSales14dSameSku;

    @JsonProperty("attributed_sales_30d_same_sku")
    private Double attributedSales30dSameSku;

    // ---- 归因销售单量 ----

    @JsonProperty("attributed_units_ordered_1d")
    private Long attributedUnitsOrdered1d;

    @JsonProperty("attributed_units_ordered_7d")
    private Long attributedUnitsOrdered7d;

    @JsonProperty("attributed_units_ordered_14d")
    private Long attributedUnitsOrdered14d;

    @JsonProperty("attributed_units_ordered_30d")
    private Long attributedUnitsOrdered30d;

    @JsonProperty("attributed_units_ordered_1d_same_sku")
    private Long attributedUnitsOrdered1dSameSku;

    @JsonProperty("attributed_units_ordered_7d_same_sku")
    private Long attributedUnitsOrdered7dSameSku;

    @JsonProperty("attributed_units_ordered_14d_same_sku")
    private Long attributedUnitsOrdered14dSameSku;

    @JsonProperty("attributed_units_ordered_30d_same_sku")
    private Long attributedUnitsOrdered30dSameSku;
}
