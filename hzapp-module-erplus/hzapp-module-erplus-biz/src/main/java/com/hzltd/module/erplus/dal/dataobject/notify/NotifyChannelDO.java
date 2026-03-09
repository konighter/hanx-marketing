package com.hzltd.module.erplus.dal.dataobject.notify;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 通知渠道配置 DO
 */
@TableName("erplus_notify_channel")
@KeySequence("erplus_notify_channel_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyChannelDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 渠道名称
     */
    private String name;

    /**
     * 渠道类型
     * 枚举 {@link com.hzltd.module.erplus.enums.notify.NotifyChannelTypeEnum}
     */
    private Integer channelType;

    /**
     * Webhook 地址
     */
    private String webhookUrl;

    /**
     * 扩展配置 JSON (如签名密钥等)
     */
    private String config;

    /**
     * 状态
     * 枚举 CommonStatusEnum 0-启用 1-禁用
     */
    private Integer status;
}
