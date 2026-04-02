package com.hzltd.module.erplus.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知渠道类型枚举
 */
@Getter
@AllArgsConstructor
public enum NotifyChannelTypeEnum {

    FEISHU(1, "feishu", "飞书"),
    DINGTALK(2, "dingtalk", "钉钉"),
    WECOM(3, "wecom", "企业微信"),
    ;

    private final Integer code;
    private final String key;
    private final String name;

    public static NotifyChannelTypeEnum of(Integer code) {
        for (NotifyChannelTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
