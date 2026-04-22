package com.hzltd.module.erplus.adv.dal.dataobject;

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
    /** 广告组ID */
    private String adGroupId;
    /** 广告ID */
    private String adId;
    /** 关键词/匹配对象ID */
    private String keywordId;
    /** 广告位 */
    private String placement;
    /** 商品 ASIN */
    private String productAsin;

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
    private BigDecimal amzAttributedSales1d;
    /** [Amazon] 7天归因销售额 */
    private BigDecimal amzAttributedSales7d;
    /** [Amazon] 14天归因销售额 */
    private BigDecimal amzAttributedSales14d;
    /** [Amazon] 30天归因销售额 */
    private BigDecimal amzAttributedSales30d;
    /** [Amazon] 1天归因订单 */
    private Long amzAttributedUnitsOrdered1d;
    /** [Amazon] 7天归因订单 */
    private Long amzAttributedUnitsOrdered7d;
    /** [Amazon] 14天归因订单 */
    private Long amzAttributedUnitsOrdered14d;
    /** [Amazon] 30天归因订单 */
    private Long amzAttributedUnitsOrdered30d;
    
    /** [Amazon] 1天归因同SKU销售额 */
    private BigDecimal amzAttributedSales1dSameSku;
    /** [Amazon] 7天归因同SKU销售额 */
    private BigDecimal amzAttributedSales7dSameSku;
    /** [Amazon] 14天归因同SKU销售额 */
    private BigDecimal amzAttributedSales14dSameSku;
    /** [Amazon] 30天归因同SKU销售额 */
    private BigDecimal amzAttributedSales30dSameSku;
    /** [Amazon] 1天归因同SKU订单 */
    private Long amzAttributedUnitsOrdered1dSameSku;
    /** [Amazon] 7天归因同SKU订单 */
    private Long amzAttributedUnitsOrdered7dSameSku;
    /** [Amazon] 14天归因同SKU订单 */
    private Long amzAttributedUnitsOrdered14dSameSku;
    /** [Amazon] 30天归因同SKU订单 */
    private Long amzAttributedUnitsOrdered30dSameSku;

}
