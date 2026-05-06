package com.hzltd.module.erplus.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务执行结果状态枚举
 *
 * @author antigravity
 */
@Getter
@AllArgsConstructor
public enum ErpTaskResultStatusEnum {

    /** 待处理 */
    PENDING("PENDING", "待处理"),
    
    /** 已提交 (针对异步/第三方任务) */
    SUBMITTED("SUBMITTED", "已提交"),
    
    /** 处理中 */
    PROCESSING("PROCESSING", "处理中"),
    
    /** 执行成功 */
    SUCCESS("SUCCESS", "执行成功"),
    
    /** 执行失败 */
    FAILED("FAILED", "执行失败"),
    
    /** 已跳过 (如业务逻辑判断不需执行) */
    SKIPPED("SKIPPED", "已跳过"),
    
    /** 等待重试 */
    WAIT_RETRY("WAIT_RETRY", "等待重试");

    private final String status;
    private final String description;

}
