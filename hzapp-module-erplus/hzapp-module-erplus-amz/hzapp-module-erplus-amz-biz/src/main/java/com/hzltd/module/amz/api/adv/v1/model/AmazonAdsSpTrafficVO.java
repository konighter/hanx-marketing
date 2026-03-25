package com.hzltd.module.amz.api.adv.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Amazon Marketing Stream - Sponsored Products Traffic Dataset VO
 * See: https://advertising.amazon.com/API/docs/en-us/guides/amazon-marketing-stream/datasets/sp-performance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonAdsSpTrafficVO {

    /** 幂等令牌，用于去重 */
    private String idempotencyId;

    /** 数据集 ID (sp-traffic) */
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

    /** 关键词文本 */
    private String keywordText;

    /** 匹配类型 */
    private String matchType;

    /** 展示位置 */
    private String placement;

    /** 时间窗口开始时间 (ISO 8601 date time) */
    private String timeWindowStart;

    /** 点击数 */
    private Long clicks;

    /** 曝光数 */
    private Long impressions;

    /** 消耗金额 */
    private BigDecimal cost;

}
