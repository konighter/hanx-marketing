package com.hzltd.module.erplus.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举 (持久化状态)
 *
 * @author antigravity
 */
@Getter
@AllArgsConstructor
public enum ErpTaskStatusEnum {

    /** 待处理 */
    PENDING("PENDING", "待处理"),
    
    /** 已提交 */
    SUBMITTED("SUBMITTED", "已提交"),
    
    /** 处理中 */
    PROCESSING("PROCESSING", "处理中"),
    
    /** 成功 */
    SUCCESS("SUCCESS", "成功"),
    
    /** 失败 */
    FAILED("FAILED", "失败"),
    
    /** 已取消 */
    CANCELLED("CANCELLED", "已取消"),
    
    /** 已暂停 */
    PAUSED("PAUSED", "已暂停");

    private final String status;
    private final String description;

}
