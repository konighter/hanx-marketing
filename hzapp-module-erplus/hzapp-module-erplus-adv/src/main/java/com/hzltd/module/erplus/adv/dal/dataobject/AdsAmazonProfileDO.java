package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 亚马逊广告 Profile DO
 *
 * 一个亚马逊广告账号（LWA 授权）下可以有多个 Profile，每个 Profile 对应一个国家/站点。
 *
 * @author hzadd
 */
@TableName(value = "ads_amazon_profile", autoResultMap = true)
@KeySequence("ads_amazon_profile_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAmazonProfileDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 关联广告账户ID
     */
    private Long accountId;

    /**
     * 卖家ID
     */
    private String sellerId;

    /**
     * 亚马逊原始 Profile ID
     */
    private String profileId;

    /**
     * 国家代码 (US, UK, JP...)
     */
    private String countryCode;

    /**
     * 区域 (NA, EU, FE)
     */
    private String region;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 亚马逊原始 Entity ID (广告主账号 ID)
     */
    private String entityId;

    /**
     * 广告主账号名称
     */
    private String entityName;

    /**
     * 状态
     */
    private String status;

    /**
     * 配置信息
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Config config;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        /** 订阅 ID 映射: dataSetId -> subscriptionId */
        private java.util.Map<String, String> streamSubscriptions;
    }

}
