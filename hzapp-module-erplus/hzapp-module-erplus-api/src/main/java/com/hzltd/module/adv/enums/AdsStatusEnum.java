package com.hzltd.module.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告实体统一状态枚举
 *
 * @author hzadd
 */
@Getter
@AllArgsConstructor
public enum AdsStatusEnum {

    ENABLED("ENABLED", "启用"),
    PAUSED("PAUSED", "暂停"),
    ARCHIVED("ARCHIVED", "归档"),
    REMOVED("REMOVED", "已删除");

    private final String code;
    private final String name;
}
