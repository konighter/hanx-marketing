package com.hzltd.module.erplus.ai.mas.runtime.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务任务类型枚举
 */
@Getter
@AllArgsConstructor
public enum MasTaskTypeEnum {
    /**
     * 并行任务
     */
    PARALLEL("PARALLEL"),
    /**
     * 串行任务
     */
    SEQUENTIAL("SEQUENTIAL"),
    /**
     * 叶子执行任务 (真正调用 Agent Graph 的单元)
     */
    LEAF("LEAF");

    private final String type;
}
