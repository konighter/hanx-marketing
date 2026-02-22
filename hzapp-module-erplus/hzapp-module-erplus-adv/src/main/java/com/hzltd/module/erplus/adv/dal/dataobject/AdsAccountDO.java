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
 * 广告账户 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_account", autoResultMap = true)
@KeySequence("ads_account_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAccountDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 广告平台: AMAZON / META / GOOGLE / TIKTOK
     */
    private String platform;

    /**
     * 所属店铺
     */
    private Long shopId;

    /**
     * 关联凭证ID
     */
    private Long credentialId;

    /**
     * 父级账号ID (用于 ACCOUNT 关联 AUTHORIZATION)
     */
    private Long parentId;

    /**
     * 平台原始账户ID
     */
    private String externalAccountId;

    /**
     * 账户显示名称
     */
    private String name;

    /**
     * 账户币种 (USD, CNY, EUR...)
     */
    private String currency;

    /**
     * 账户时区
     */
    private String timezone;

    /**
     * 授权状态 (1: 有效, 0: 失效)
     */
    private Integer authStatus;

    /**
     * 上次同步完成时间
     */
    private LocalDateTime lastSyncedAt;

    /**
     * 平台专属配置 (JSON)
     */
//    @TableField(typeHandler = JacksonTypeHandler.class)
    private String extConfig;

    /**
     * 备注
     */
    private String remark;
}
