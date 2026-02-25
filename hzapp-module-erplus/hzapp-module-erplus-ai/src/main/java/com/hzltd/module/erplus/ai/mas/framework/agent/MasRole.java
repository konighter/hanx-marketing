package com.hzltd.module.erplus.ai.mas.framework.agent;

import lombok.Getter;

/**
 * MAS Agent 角色定义
 */
@Getter
public enum MasRole {
    
    MANAGER("管理者", "全局决策与任务分配"),
    PM("项目经理", "需求拆解与规划"),
    EXPERT("领域专家", "策略提供与质量把控"),
    EXECUTOR("执行者", "具体子任务实施"),
    REVIEWER("审核人", "结果验证与验收");

    private final String name;
    private final String responsibility;

    MasRole(String name, String responsibility) {
        this.name = name;
        this.responsibility = responsibility;
    }
}
