package com.hzltd.module.erplus.controller.admin.notify.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通知渠道 创建/修改 Request VO
 */
@Data
public class NotifyChannelSaveReqVO {

    /**
     * ID (修改时必传)
     */
    private Long id;

    /**
     * 渠道名称
     */
    @NotBlank(message = "渠道名称不能为空")
    private String name;

    /**
     * 渠道类型
     */
    @NotNull(message = "渠道类型不能为空")
    private Integer channelType;

    /**
     * Webhook 地址
     */
    @NotBlank(message = "Webhook 地址不能为空")
    private String webhookUrl;

    /**
     * 扩展配置 JSON
     */
    private String config;

    /**
     * 状态 0-启用 1-禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
