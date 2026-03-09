package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Amazon SP-Conversion 原始数据 DO
 * 对应 Doris 表 ads_amazon_stream_sp_conversion
 */
@TableName(value = "ads_amazon_stream_sp_conversion", autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportStreamConvertDO {

    private String datasetId;

    @TableField(exist = false)
    private String idempotencyId;

    private String marketplaceId;

    private String currency;

    private String advertiserId;

    @TableField("campaign_id")
    private String campaignId;

    @TableField("ad_group_id")
    private String adGroupId;

    @TableField("ad_id")
    private String adId;

    @TableField("keyword_id")
    private String keywordId;

    private String placement;

    private Long timeWindowStart;

    // ---- 归因转化次数 ----
    private Long attributedConversions1d;
    private Long attributedConversions7d;
    private Long attributedConversions14d;
    private Long attributedConversions30d;
    private Long attributedConversions1dSameSku;
    private Long attributedConversions7dSameSku;
    private Long attributedConversions14dSameSku;
    private Long attributedConversions30dSameSku;

    // ---- 归因销售额 ----
    private Double attributedSales1d;
    private Double attributedSales7d;
    private Double attributedSales14d;
    private Double attributedSales30d;
    private Double attributedSales1dSameSku;
    private Double attributedSales7dSameSku;
    private Double attributedSales14dSameSku;
    private Double attributedSales30dSameSku;

    // ---- 归因销售单量 ----
    private Long attributedUnitsOrdered1d;
    private Long attributedUnitsOrdered7d;
    private Long attributedUnitsOrdered14d;
    private Long attributedUnitsOrdered30d;
    private Long attributedUnitsOrdered1dSameSku;
    private Long attributedUnitsOrdered7dSameSku;
    private Long attributedUnitsOrdered14dSameSku;
    private Long attributedUnitsOrdered30dSameSku;
}
