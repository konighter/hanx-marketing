package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实时统一广告多维流数据宽表 DO
 * 对应 Doris 表 ads_report_stream_realtime
 *
 * @author antigravity
 */
@TableName(value = "ads_report_stream_realtime", autoResultMap = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportStreamRealtimeDO {

    // ===== 维度列 (Unique Key) =====
    
    /**
     * 流窗口起始时间 (UTC)
     */
    private LocalDateTime windowStartTime;

    /**
     * 店铺ID
     */
    private Integer shopId;

    /**
     * 广告平台 (AMAZON, META, GOOGLE)
     */
    private String platform;

    /**
     * 广告活动ID
     */
    private String campaignId;

    /**
     * 广告组ID
     */
    private String adGroupId;

    /**
     * 广告ID
     */
    private String adId;

    /**
     * 关键词/匹配对象ID
     */
    private String keywordId;

    /**
     * 广告位
     */
    private String placement;

    /**
     * 商品 ASIN
     */
    private String productAsin;

    // ===== 基础指标列 =====

    /**
     * 曝光量
     */
    private Long impressions;

    /**
     * 点击量
     */
    private Long clicks;

    /**
     * 花费
     */
    private BigDecimal cost;

    /**
     * 估算销售额
     */
    private BigDecimal sales;

    /**
     * 估算订单量
     */
    private Long orders;

    // ===== Amazon 扩展归因指标 =====

    private BigDecimal amzAttributedSales1d;
    private BigDecimal amzAttributedSales7d;
    private BigDecimal amzAttributedSales14d;
    private BigDecimal amzAttributedSales30d;

    private Long amzAttributedUnitsOrdered1d;
    private Long amzAttributedUnitsOrdered7d;
    private Long amzAttributedUnitsOrdered14d;
    private Long amzAttributedUnitsOrdered30d;

    private BigDecimal amzAttributedSales1dSameSku;
    private BigDecimal amzAttributedSales7dSameSku;
    private BigDecimal amzAttributedSales14dSameSku;
    private BigDecimal amzAttributedSales30dSameSku;

    private Long amzAttributedUnitsOrdered1dSameSku;
    private Long amzAttributedUnitsOrdered7dSameSku;
    private Long amzAttributedUnitsOrdered14dSameSku;
    private Long amzAttributedUnitsOrdered30dSameSku;

    // ===== Meta 扩展归因指标 =====

    private Long metaReach;
    private BigDecimal metaFrequency;
    private Long metaPurchases1dClick;
    private Long metaPurchases7dClick;
    private Long metaPurchases1dView;

    // ===== Google 扩展归因指标 =====

    private Long ggViewThroughConversions;
    private BigDecimal ggConversions;
    private BigDecimal ggConversionValue;

}
