package com.hzltd.module.erplus.ai.mas.report;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MAS 执行事件数据结构.
 * <p>
 * 所有 Orchestrator / DAG / Node 层面的关键事件均以此格式封装，
 * 经由 {@link MasEventPublisher} 双轨派发：持久化日志 + WebSocket 推流。
 */
@Value
@Builder
public class MasEvent {

    /** Task 级别的 Session 隔离 ID（由 MasTaskJob 生成，格式：{sessionId}_Task#{taskId}） */
    String sessionId;

    /** 事件唯一 ID */
    @Builder.Default
    String eventId = UUID.randomUUID().toString();

    /** 事件类型 */
    MasEventType type;

    /**
     * 执行节点 ID（仅 NODE_* 类型事件有值；Orchestrator/Planner 层事件为 null）.
     * 节点 ID 与 session.state 的 key 约定一致："{nodeId}_output"
     */
    String nodeId;

    /** 节点对应的 Agent 角色（NODE_* 事件填充，可用于前端展示 Agent 名称） */
    String agentRole;

    /**
     * 事件携带的详细数据（JSON 字符串，可能较大）.
     * 此字段写入持久化日志，但 <b>不通过 WebSocket 推送</b>，避免消息过大。
     * 前端需要时通过 REST 接口按 eventId 查询。
     */
    String payload;

    /** 简短摘要（用于 WebSocket 推送，保持轻量） */
    String summary;

    /** 当前 Phase 编号（从 1 开始） */
    int phaseIndex;

    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();
}
