package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 广告小时绩效报表 DO (Doris 聚合模型)
 *
 * 使用 group_column 标识聚合维度，不属于当前维度的 ID 列填 -1。
 * Doris Aggregate Model 下指标列自动 SUM 聚合，直接 INSERT 即可累加。
 */
@TableName(value = "ads_report_hourly")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportHourlyDO {

    /** 店铺ID */
    private Long shopId;

    /** 关联广告账户ID */
    private Long accountId;

    /** 聚合维度: campaign / adGroup / ad / keyword */
    private String groupColumn;

    /** 报表小时点 */
    private LocalDateTime reportHour;

    /** 广告活动ID (-1 = 不在聚合维度) */
    private Long campaignId;

    /** 广告组ID (-1 = 不在聚合维度) */
    private Long adGroupId;

    /** 广告ID (-1 = 不在聚合维度) */
    private Long adId;

    /** 关键词ID (-1 = 不在聚合维度) */
    private Long keywordId;

    /** 展示数 */
    private Long impressions;

    /** 点击数 */
    private Long clicks;

    /** 花费 */
    private BigDecimal cost;

    /** 7天归因销售额 */
    @TableField("sales7d")
    private BigDecimal sales7d;

    /** 7天归因订单量 */
    @TableField("orders7d")
    private Long orders7d;

    /** 14天归因销售额 */
    @TableField("sales14d")
    private BigDecimal sales14d;

    /** 14天归因订单量 */
    @TableField("orders14d")
    private Long orders14d;

    /** 30天归因销售额 */
    @TableField("sales30d")
    private BigDecimal sales30d;

    /** 30天归因订单量 */
    @TableField("orders30d")
    private Long orders30d;
}
