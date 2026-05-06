package com.hzltd.module.erplus.adv.enums.automation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告自动化计划资源角色枚举
 */
@Getter
@AllArgsConstructor
public enum AdsAutomationRoleEnum {

    /**
     * 源活动 (通常是自动投放活动)
     */
    SOURCE("SOURCE", "源活动"),

    /**
     * 目标共享活动 (通常是手动精准活动)
     */
    SINK_SHARED("SINK_SHARED", "目标共享活动"),

    /**
     * 目标隔离活动 (针对高绩效词的独立活动)
     */
    SINK_ISOLATED("SINK_ISOLATED", "目标隔离活动");

    private final String role;
    private final String name;

}
