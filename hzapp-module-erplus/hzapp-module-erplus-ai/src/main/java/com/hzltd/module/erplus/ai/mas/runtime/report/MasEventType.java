package com.hzltd.module.erplus.ai.mas.runtime.report;

/**
 * MAS 执行事件类型.
 * <p>
 * 事件双轨派发：持久化日志（审计）+ WebSocket 推流（实时展示）
 */
public enum MasEventType {

    // ------- Orchestrator 层事件 -------

    /** Phase N 开始 */
    PHASE_STARTED,
    /** Phase N 完成（所有节点执行完毕） */
    PHASE_COMPLETED,
    /** Phase 挂起，等待用户审批（场景A: Phase 门控） */
    PHASE_SUSPENDED,

    // ------- Planner 层事件 -------

    /** Planner 开始推理 */
    PLANNER_STARTED,
    /** Planner 推理完成，输出 DagGenerationPlan */
    PLANNER_COMPLETED,
    /** Planner 推理失败（payload 包含重试次数和错误信息） */
    PLANNER_FAILED,

    // ------- DAG 节点层事件 -------

    /** DAG 节点开始执行（nodeId / agentRole 标识具体节点） */
    NODE_STARTED,
    /** DAG 节点执行完成（payload 包含输出摘要） */
    NODE_COMPLETED,
    /** DAG 节点执行失败（payload 包含错误信息） */
    NODE_FAILED,

    // ------- 用户交互事件 -------

    /** 收到用户输入（payload 包含优先级: URGENT/SUPPLEMENT） */
    USER_INPUT_RECEIVED,
    /** URGENT 中断已被 Orchestrator 响应，将在下个 Phase 边界重新规划 */
    INTERRUPT_TRIGGERED,

    // ------- Task 生命周期事件 -------

    /** 整个 Task 执行完成（FINISH） */
    TASK_FINISHED,
    /** 整个 Task 执行失败（FAIL） */
    TASK_FAILED,
}
