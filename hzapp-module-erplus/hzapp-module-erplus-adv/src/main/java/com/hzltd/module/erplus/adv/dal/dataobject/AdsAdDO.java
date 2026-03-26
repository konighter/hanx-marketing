package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 广告实体 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_ad", autoResultMap = true)
@KeySequence("ads_ad_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 关联广告组ID
     */
    private Long adGroupId;
    /**
     * 关联广告计划ID (冗余)
     */
    private Long campaignId;

    /**
     * 关联广告账户ID (冗余)
     */
    private Long accountId;

    /**
     * 广告平台 (AMAZON, GOOGLE, etc.)
     */
    private String platform;

    /**
     * 平台原始广告ID
     */
    private String externalId;

    /**
     * 广告名称
     */
    private String name;

    /**
     * 广告格式 (IMAGE / VIDEO / CAROUSEL / RESPONSIVE)
     */
    private String adFormat;

    /**
     * 统一状态
     */
    private String status;

    /**
     * 平台原始状态
     */
    private String platformStatus;

    /**
     * 标题文案
     */
    private String headline;

    /**
     * 描述文案
     */
    private String description;

    /**
     * 落地页URL
     */
    private String landingPageUrl;

    /**
     * 行动号召 (SHOP_NOW / LEARN_MORE)
     */
    private String callToAction;

    /**
     * 平台审核状态 (APPROVED / PENDING / REJECTED)
     */
    private String reviewStatus;

    /**
     * 广告ASIN (亚马逊专用)
     */
    @TableField(exist = false)
    private String asin;

    /**
     * 广告SKU (亚马逊专用)
     */
    @TableField(exist = false)
    private String sku;

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
