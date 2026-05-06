package com.hzltd.module.erplus.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举
 *
 * @author antigravity
 */
@Getter
@AllArgsConstructor
public enum ErpTaskStatus {

    PENDING("PENDING", "待处理"),
    SUBMITTED("SUBMITTED", "已提交远程"),
    PROCESSING("PROCESSING", "本地处理中"),
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    PAUSED("PAUSED", "已暂停"),
    CANCELLED("CANCELLED", "已取消");

    private final String status;
    private final String description;

    public static boolean isTerminal(String status) {
        return SUCCESS.getStatus().equals(status) || FAILED.getStatus().equals(status) || CANCELLED.getStatus().equals(status);
    }

    public static boolean isSuccess(String status) {
        return SUCCESS.getStatus().equals(status);
    }

    public static boolean isFailed(String status) {
        return FAILED.getStatus().equals(status);
    }
}
