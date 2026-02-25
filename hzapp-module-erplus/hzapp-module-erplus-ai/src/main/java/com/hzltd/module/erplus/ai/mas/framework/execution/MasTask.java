package com.hzltd.module.erplus.ai.mas.framework.execution;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * MAS 任务单元定义
 */
@Data
@Builder
public class MasTask {
    
    private final String taskId;
    private final String name;
    private final String description;
    
    /**
     * 执行该任务所需的 Prompt
     */
    private final String prompt;
    
    /**
     * 角色限制
     */
    private final String roleRequited;

    /**
     * 执行该任务所需的工具 (对应 Spring Bean 名称)
     */
    @Builder.Default
    private final List<String> tools = List.of();

    /**
     * 是否为内部编排任务 (如初始化、评估等)，内部任务不触发反馈循环
     */
    @Builder.Default
    private final boolean internal = false;
}
