package com.hzltd.module.erplus.adv.controller.admin.mas.vo;

import lombok.Data;

@Data
public class FlowConfigVO {
    /** BPMN 流程模板 Key (e.g. "skill-seq-tasks") */
    private String flowDefineKey;
    /** 运行时流程实例 ID (启动后回填) */
    private String flowInstanceId;
    /** 模板编排类型: SEQUENTIAL / PARALLEL */
    private String orchestrationType;
    /** 监控间隔 ISO-8601 (e.g. "PT10M") */
    private String monitorInterval;
    /** 会话 ID (chat 绑定) */
    private String sessionId;
}
