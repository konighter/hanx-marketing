package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 广告组 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_ad_group", autoResultMap = true)
@KeySequence("ads_ad_group_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdGroupDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 关联广告计划ID
     */
    private Long campaignId;

    /**
     * 广告平台 (AMAZON, GOOGLE, etc.)
     */
    private String platform;

    /**
     * 关联广告账户ID (冗余, 优化跨层级查询)
     */
    private Long accountId;

    /**
     * 所属店铺ID
     */
    private Long shopId;

    /**
     * 平台原始广告组ID
     */
    private String externalId;

    /**
     * 广告组名称
     */
    private String name;

    /**
     * 统一状态: ENABLED / PAUSED / ARCHIVED / REMOVED
     */
    private String status;

    /**
     * 原始平台状态
     */
    private String platformStatus;

    /**
     * 默认出价
     */
    private BigDecimal defaultBid;

    /**
     * 出价策略
     */
    private String bidStrategy;

    /**
     * 投放定向方式 (KEYWORD / AUTO / AUDIENCE)
     */
    private String targetingType;

    /**
     * 定向参数等平台扩展 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object extData;

    /**
     * 上次从平台同步的时间
     */
    private LocalDateTime syncedAt;
}
