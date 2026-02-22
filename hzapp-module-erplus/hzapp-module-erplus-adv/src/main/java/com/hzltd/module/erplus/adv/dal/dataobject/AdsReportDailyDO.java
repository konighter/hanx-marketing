package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 广告每日绩效报表 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_report_daily", autoResultMap = true)
@KeySequence("ads_report_daily_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportDailyDO extends BaseDO {

    @TableId
    private Long id;
    /** 关联广告账户ID */
    private Long accountId;
    /** 实体类型: CAMPAIGN / ADGROUP / AD / KEYWORD */
    private String entityType;
    /** 内部实体ID */
    private Long entityId;
    /** 平台原始实体ID */
    private String externalEntityId;
    /** 报表日期 */
    private LocalDate reportDate;
    /** 展现量 */
    private Long impressions;
    /** 点击量 */
    private Long clicks;
    /** 花费 */
    private BigDecimal spend;
    /** 转化数量 */
    private Integer conversions;
    /** 转化总金额/销售额 */
    private BigDecimal conversionValue;
    /** 视频播放量 */
    private Long videoViews;
    /** 触达人数 */
    private Long reach;
    /** 数据写入/覆盖时间 */
    private LocalDateTime syncedAt;
}
