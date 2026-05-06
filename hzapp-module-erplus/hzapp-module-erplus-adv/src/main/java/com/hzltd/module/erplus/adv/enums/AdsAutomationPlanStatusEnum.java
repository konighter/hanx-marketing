package com.hzltd.module.erplus.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告自动化计划状态枚举
 *
 * @author antigravity
 */
@Getter
@AllArgsConstructor
public enum AdsAutomationPlanStatusEnum {

    /** 运行中 */
    RUNNING("RUNNING", "运行中"),
    
    /** 已暂停 */
    PAUSED("PAUSED", "已暂停"),
    
    /** 已终止 */
    TERMINATED("TERMINATED", "已终止");

    private final String status;
    private final String description;

}
