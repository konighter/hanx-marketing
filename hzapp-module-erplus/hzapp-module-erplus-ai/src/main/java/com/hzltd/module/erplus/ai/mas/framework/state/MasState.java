package com.hzltd.module.erplus.ai.mas.framework.state;

import lombok.Getter;

/**
 * MAS 任务执行状态定义
 */
@Getter
public enum MasState {
    
    INIT("初始化", "Manager 正在确定角色与流程"),
    PLANNING("规划中", "PM 正在进行任务拆解"),
    EXECUTING("执行中", "Executor 正在执行子任务"),
    REVIEWING("审核中", "Reviewer 正在品质评估"),
    WAITING_FOR_USER("等待介入", "等待人工反馈或指令"),
    COMPLETED("已完成", "整体目标已达成"),
    FAILED("已失败", "执行出现不可逆错误");

    private final String label;
    private final String description;

    MasState(String label, String description) {
        this.label = label;
        this.description = description;
    }
}
