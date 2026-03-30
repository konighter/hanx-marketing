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
 * 关键词/投放目标 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_keyword", autoResultMap = true)
@KeySequence("ads_keyword_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsKeywordDO extends BaseDO {

    @TableId
    private Long id;
    /** 关联广告组ID */
    private Long adGroupId;
    /** 关联广告计划ID (冗余) */
    private Long campaignId;
    /** 关联广告账户ID (冗余) */
    private Long accountId;
    /** 所属店铺ID */
    private Long shopId;

    /**
     * 广告平台 (AMAZON, GOOGLE, etc.)
     */
    private String platform;

    /** 平台关键词ID */
    private String externalId;
    /** 关键词文本 */
    private String keywordText;
    /** 匹配类型: EXACT / PHRASE / BROAD */
    private String matchType;
    /** 自定义出价 */
    private BigDecimal bid;
    /** 统一状态 */
    private String status;
    /** 原始平台状态 */
    private String platformStatus;
    /** 是否为否定关键词 */
    private Boolean isNegative;
    /** 平台扩展字段 (JSON) */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object extData;
    /**
     * 上次从平台同步的时间
     */
    private LocalDateTime syncedAt;
}
