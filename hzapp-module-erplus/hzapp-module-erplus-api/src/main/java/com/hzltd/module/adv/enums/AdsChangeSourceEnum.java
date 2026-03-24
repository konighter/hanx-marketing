package com.hzltd.module.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变更来源枚举
 *
 * @author hzadd
 */
@Getter
@AllArgsConstructor
public enum AdsChangeSourceEnum {

    PLATFORM_SYNC("PLATFORM_SYNC", "平台同步"),
    USER("USER", "用户操作"),
    AI_AGENT("AI_AGENT", "AI Agent");

    private final String code;
    private final String name;
}
