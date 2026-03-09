package com.hzltd.module.erplus.adv.adapter.amazon.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportHourlyDO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Amazon Advertising SP-API Traffic Metric Bean
 * 用于处理 sp-traffic 数据集
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonAdTrafficMetric {

    @JsonProperty("advertiser_id")
    private String advertiserId;

    @JsonProperty("marketplace_id")
    private String marketplaceId;

    @JsonProperty("dataset_id")
    private String datasetId;

    @JsonProperty("campaign_id")
    private String campaignId;

    @JsonProperty("ad_group_id")
    private String adGroupId;

    @JsonProperty("ad_id")
    private String adId;

    @JsonProperty("keyword_id")
    private String keywordId;

    @JsonProperty("keyword_text")
    private String keywordText;

    @JsonProperty("match_type")
    private String matchType;

    @JsonProperty("placement")
    private String placement;

    /**
     * 时间窗口开始时间，建议使用 ZonedDateTime 处理 ISO 8601 时区
     */
    @JsonProperty("time_window_start")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AmazonZonedDateTimeDeserializer.class)
    private Long timeWindowStart;

    @JsonProperty("impressions")
    private Integer impressions;

    @JsonProperty("clicks")
    private Integer clicks;

    /**
     * 广告花费，使用 BigDecimal 确保财务精度
     */
    @JsonProperty("cost")
    private BigDecimal cost;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("idempotency_id")
    private String idempotencyId;


    public AdsReportHourlyDO toHourlyDO() {
        AdsReportHourlyDO reportHourlyDO = new AdsReportHourlyDO();



return reportHourlyDO;



    }



}