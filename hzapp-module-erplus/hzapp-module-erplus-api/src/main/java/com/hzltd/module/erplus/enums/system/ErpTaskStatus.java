package com.hzltd.module.erplus.enums.system;

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
    FAILED("FAILED", "失败");

    private final String status;
    private final String description;

    public static boolean isTerminal(String status) {
        return SUCCESS.getStatus().equals(status) || FAILED.getStatus().equals(status);
    }
}
