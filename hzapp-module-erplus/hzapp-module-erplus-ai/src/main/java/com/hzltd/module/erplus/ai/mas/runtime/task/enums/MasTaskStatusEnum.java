package com.hzltd.module.erplus.ai.mas.runtime.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务任务状态枚举
 */
@Getter
@AllArgsConstructor
public enum MasTaskStatusEnum {
    /**
     * 等待调度
     */
    PENDING("PENDING"),
    /**
     * 等待前置任务完成 (用于串行任务)
     */
    WAITING("WAITING"),
    /**
     * 执行中/子任务执行中
     */
    RUNNING("RUNNING"),
    /**
     * 等待人工审核 (仅父任务适用)
     */
    REVIEW_REQUIRED("REVIEW_REQUIRED"),
    /**
     * 执行成功
     */
    SUCCESS("SUCCESS"),
    /**
     * 等待恢复执行 (用于长周期任务)
     */
    RESUME("RESUME"),
    /**
     * 执行失败
     */
    FAILED("FAILED");

    private final String status;
}
