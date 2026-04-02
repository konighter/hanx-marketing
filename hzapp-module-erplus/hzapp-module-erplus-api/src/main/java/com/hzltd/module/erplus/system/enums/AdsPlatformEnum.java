package com.hzltd.module.erplus.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告平台枚举
 *
 * @author hzadd
 */
@Getter
@AllArgsConstructor
public enum AdsPlatformEnum {

    AMAZON("AMAZON", "Amazon Ads"),
    META("META", "Meta Ads"),
    GOOGLE("GOOGLE", "Google Ads"),
    TIKTOK("TIKTOK", "TikTok Ads");

    private final String code;
    private final String name;

    public static AdsPlatformEnum of(String code) {
        for (AdsPlatformEnum value : values()) {
            if (value.getCode().equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
