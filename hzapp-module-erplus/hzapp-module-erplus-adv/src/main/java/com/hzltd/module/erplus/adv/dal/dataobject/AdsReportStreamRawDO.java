package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

@TableName(value = "ads_amazon_stream_sp_traffic", autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportStreamRawDO {

    private String datasetId;


    @TableField(exist = false)
    private String idempotencyId;

    private Long timeWindowStart;

    @TableField("campaign_id")
    private String campaignId;

    @TableField("ad_group_id")
    private String adGroupId;

    @TableField("ad_id")
    private String adId;

    @TableField("keyword_id")
    private String keywordId;

    private String keywordText;

    private String matchType;

    private String placement;

    private Long impressions;

    private Long clicks;

    private BigDecimal cost;

    private String currency;

    private String marketplaceId;

    private String advertiserId;
}
