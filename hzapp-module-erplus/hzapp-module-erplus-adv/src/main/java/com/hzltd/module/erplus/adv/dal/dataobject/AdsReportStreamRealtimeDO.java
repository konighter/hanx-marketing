package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
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
    private Long shopId;

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

    /**
     * 搜索词
     */
    private String searchTerm;

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

    @TableField("amz_attributed_sales_1d")
    private BigDecimal amzAttributedSales1d;
    @TableField("amz_attributed_sales_7d")
    private BigDecimal amzAttributedSales7d;
    @TableField("amz_attributed_sales_14d")
    private BigDecimal amzAttributedSales14d;
    @TableField("amz_attributed_sales_30d")
    private BigDecimal amzAttributedSales30d;

    @TableField("amz_attributed_units_ordered_1d")
    private Long amzAttributedUnitsOrdered1d;
    @TableField("amz_attributed_units_ordered_7d")
    private Long amzAttributedUnitsOrdered7d;
    @TableField("amz_attributed_units_ordered_14d")
    private Long amzAttributedUnitsOrdered14d;
    @TableField("amz_attributed_units_ordered_30d")
    private Long amzAttributedUnitsOrdered30d;

    @TableField("amz_attributed_conversions_1d")
    private Long amzAttributedConversions1d;
    @TableField("amz_attributed_conversions_7d")
    private Long amzAttributedConversions7d;
    @TableField("amz_attributed_conversions_14d")
    private Long amzAttributedConversions14d;
    @TableField("amz_attributed_conversions_30d")
    private Long amzAttributedConversions30d;

    @TableField("amz_attributed_sales_1d_same_sku")
    private BigDecimal amzAttributedSales1dSameSku;
    @TableField("amz_attributed_sales_7d_same_sku")
    private BigDecimal amzAttributedSales7dSameSku;
    @TableField("amz_attributed_sales_14d_same_sku")
    private BigDecimal amzAttributedSales14dSameSku;
    @TableField("amz_attributed_sales_30d_same_sku")
    private BigDecimal amzAttributedSales30dSameSku;

    @TableField("amz_attributed_units_ordered_1d_same_sku")
    private Long amzAttributedUnitsOrdered1dSameSku;
    @TableField("amz_attributed_units_ordered_7d_same_sku")
    private Long amzAttributedUnitsOrdered7dSameSku;
    @TableField("amz_attributed_units_ordered_14d_same_sku")
    private Long amzAttributedUnitsOrdered14dSameSku;
    @TableField("amz_attributed_units_ordered_30d_same_sku")
    private Long amzAttributedUnitsOrdered30dSameSku;

    @TableField("amz_attributed_conversions_1d_same_sku")
    private Long amzAttributedConversions1dSameSku;
    @TableField("amz_attributed_conversions_7d_same_sku")
    private Long amzAttributedConversions7dSameSku;
    @TableField("amz_attributed_conversions_14d_same_sku")
    private Long amzAttributedConversions14dSameSku;
    @TableField("amz_attributed_conversions_30d_same_sku")
    private Long amzAttributedConversions30dSameSku;

    // ===== Meta 扩展归因指标 =====

    private Long metaReach;
    private BigDecimal metaFrequency;
    @TableField("meta_purchases_1d_click")
    private Long metaPurchases1dClick;
    @TableField("meta_purchases_7d_click")
    private Long metaPurchases7dClick;
    @TableField("meta_purchases_1d_view")
    private Long metaPurchases1dView;

    // ===== Google 扩展归因指标 =====

    private Long ggViewThroughConversions;
    private BigDecimal ggConversions;
    private BigDecimal ggConversionValue;

}
