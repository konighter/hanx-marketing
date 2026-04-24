package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 离线统一广告多维批处理聚合表 (Doris)
 * 
 * 对应 Doris 表 ads_report_batch
 */
@TableName("ads_report_batch")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportBatchDO {

    /** 批次覆盖目标的一天 */
    private LocalDate reportDate;
    /** 店铺ID */
    private Long shopId;
    /** 广告平台 (AMAZON, META, GOOGLE) */
    private String platform;
    /** 广告活动ID */
    private String campaignId;
    /** 广告活动名称 */
    @TableField(exist = false)
    private String campaignName;
    /** 广告组ID */
    private String adGroupId;
    /** 广告组名称 */
    @TableField(exist = false)
    private String adGroupName;
    /** 广告ID */
    private String adId;
    /** 广告名称/标题 */
    @TableField(exist = false)
    private String adName;
    /** 关键词/匹配对象ID */
    private String keywordId;
    /** 关键词文本 */
    @TableField(exist = false)
    private String keywordText;
    /** 广告位 */
    private String placement;
    /** 商品 ASIN */
    private String productAsin;
    /** 记录类型 (CAMPAIGN, AD_GROUP, AD, TARGETING, PLACEMENT, SEARCH_TERM) */
    private String recordType;
    /** 搜索词 */
    private String searchTerm;

    // ===== 基础指标列 =====
    /** 曝光量 */
    private Long impressions;
    /** 点击量 */
    private Long clicks;
    /** 花费 */
    private BigDecimal cost;
    /** 通用销售额 */
    private BigDecimal sales;
    /** 通用订单量 */
    private Long orders;

    // ===== Amazon 扩展归因指标 =====
    /** [Amazon] 1天归因销售额 */
    @TableField("amz_attributed_sales_1d")
    private BigDecimal amzAttributedSales1d;
    /** [Amazon] 7天归因销售额 */
    @TableField("amz_attributed_sales_7d")
    private BigDecimal amzAttributedSales7d;
    /** [Amazon] 14天归因销售额 */
    @TableField("amz_attributed_sales_14d")
    private BigDecimal amzAttributedSales14d;
    /** [Amazon] 30天归因销售额 */
    @TableField("amz_attributed_sales_30d")
    private BigDecimal amzAttributedSales30d;
    /** [Amazon] 1天归因订单(Units) */
    @TableField("amz_attributed_units_ordered_1d")
    private Long amzAttributedUnitsOrdered1d;
    /** [Amazon] 7天归因订单(Units) */
    @TableField("amz_attributed_units_ordered_7d")
    private Long amzAttributedUnitsOrdered7d;
    /** [Amazon] 14天归因订单(Units) */
    @TableField("amz_attributed_units_ordered_14d")
    private Long amzAttributedUnitsOrdered14d;
    /** [Amazon] 30天归因订单(Units) */
    @TableField("amz_attributed_units_ordered_30d")
    private Long amzAttributedUnitsOrdered30d;
    /** [Amazon] 1天归因转化数(Orders) */
    @TableField("amz_attributed_conversions_1d")
    private Long amzAttributedConversions1d;
    /** [Amazon] 7天归因转化数(Orders) */
    @TableField("amz_attributed_conversions_7d")
    private Long amzAttributedConversions7d;
    /** [Amazon] 14天归因转化数(Orders) */
    @TableField("amz_attributed_conversions_14d")
    private Long amzAttributedConversions14d;
    /** [Amazon] 30天归因转化数(Orders) */
    @TableField("amz_attributed_conversions_30d")
    private Long amzAttributedConversions30d;
    
    /** [Amazon] 1天归因同SKU销售额 */
    @TableField("amz_attributed_sales_1d_same_sku")
    private BigDecimal amzAttributedSales1dSameSku;
    /** [Amazon] 7天归因同SKU销售额 */
    @TableField("amz_attributed_sales_7d_same_sku")
    private BigDecimal amzAttributedSales7dSameSku;
    /** [Amazon] 14天归因同SKU销售额 */
    @TableField("amz_attributed_sales_14d_same_sku")
    private BigDecimal amzAttributedSales14dSameSku;
    /** [Amazon] 30天归因同SKU销售额 */
    @TableField("amz_attributed_sales_30d_same_sku")
    private BigDecimal amzAttributedSales30dSameSku;
    /** [Amazon] 1天归因同SKU订单(Units) */
    @TableField("amz_attributed_units_ordered_1d_same_sku")
    private Long amzAttributedUnitsOrdered1dSameSku;
    /** [Amazon] 7天归因同SKU订单(Units) */
    @TableField("amz_attributed_units_ordered_7d_same_sku")
    private Long amzAttributedUnitsOrdered7dSameSku;
    /** [Amazon] 14天归因同SKU订单(Units) */
    @TableField("amz_attributed_units_ordered_14d_same_sku")
    private Long amzAttributedUnitsOrdered14dSameSku;
    /** [Amazon] 30天归因同SKU订单(Units) */
    @TableField("amz_attributed_units_ordered_30d_same_sku")
    private Long amzAttributedUnitsOrdered30dSameSku;
    /** [Amazon] 1天归因同SKU转化数(Orders) */
    @TableField("amz_attributed_conversions_1d_same_sku")
    private Long amzAttributedConversions1dSameSku;
    /** [Amazon] 7天归因同SKU转化数(Orders) */
    @TableField("amz_attributed_conversions_7d_same_sku")
    private Long amzAttributedConversions7dSameSku;
    /** [Amazon] 14天归因同SKU转化数(Orders) */
    @TableField("amz_attributed_conversions_14d_same_sku")
    private Long amzAttributedConversions14dSameSku;
    /** [Amazon] 30天归因同SKU转化数(Orders) */
    @TableField("amz_attributed_conversions_30d_same_sku")
    private Long amzAttributedConversions30dSameSku;

    // ===== Meta 扩展归因指标 =====
    /** [Meta] 到达人数 (Reach) */
    private Long metaReach;
    /** [Meta] 展示频率 */
    private BigDecimal metaFrequency;
    /** [Meta] 1天点击归因购买数 */
    @TableField("meta_purchases_1d_click")
    private Long metaPurchases1dClick;
    /** [Meta] 7天点击归因购买数 */
    @TableField("meta_purchases_7d_click")
    private Long metaPurchases7dClick;
    /** [Meta] 1天浏览归因购买数 */
    @TableField("meta_purchases_1d_view")
    private Long metaPurchases1dView;

    // ===== Google 扩展归因指标 =====
    /** [Google] 浏览型转化数 */
    private Long ggViewThroughConversions;
    /** [Google] 转化数(支持小数) */
    private BigDecimal ggConversions;
    /** [Google] 转化总价值 */
    private BigDecimal ggConversionValue;

}
