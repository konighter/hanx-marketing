package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value = "ads_amazon_stream_raw", autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportStreamRawDO {

    private String datasetId;

    private String idempotencyId;

    private LocalDateTime timeWindowStart;

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
