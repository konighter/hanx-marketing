package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 广告计划 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_campaign", autoResultMap = true)
@KeySequence("ads_campaign_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCampaignDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 关联广告账户ID
     */
    private Long accountId;

    /**
     * 平台原始 Campaign ID
     */
    private String externalId;

    /**
     * 计划名称
     */
    private String name;

    /**
     * 计划类型 (SP/SB/SD/SEARCH/DISPLAY/VIDEO)
     */
    private String campaignType;

    /**
     * 广告目标 (CONVERSIONS / TRAFFIC / AWARENESS)
     */
    private String objective;

    /**
     * 统一状态: ENABLED / PAUSED / ARCHIVED / REMOVED
     */
    private String status;

    /**
     * 原始平台状态原文
     */
    private String platformStatus;

    /**
     * 预算类型: DAILY / LIFETIME / CAMPAIGN_TOTAL
     */
    private String budgetType;

    /**
     * 日预算
     */
    private BigDecimal dailyBudget;

    /**
     * 总预算
     */
    private BigDecimal totalBudget;

    /**
     * 投放开始日期
     */
    private LocalDate startDate;

    /**
     * 投放结束日期
     */
    private LocalDate endDate;

    /**
     * 分时投放配置 (JSON 字符串)
     */
    private String deliverySchedule;

    /**
     * 出价策略 (MANUAL_CPC / AUTO_BID / TARGET_ROAS)
     */
    private String biddingStrategy;

    /**
     * 平台扩展字段 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object extData;

    /**
     * 上次从平台同步的时间
     */
    private LocalDateTime syncedAt;
}
