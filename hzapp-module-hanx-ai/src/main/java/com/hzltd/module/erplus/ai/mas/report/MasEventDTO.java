package com.hzltd.module.erplus.ai.mas.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * WebSocket 推送的事件 DTO（轻量版，不含大 payload）.
 * <p>
 * payload（完整输出）不通过 WebSocket 推送，避免消息过大。
 * 前端需要完整数据时，通过 REST 接口按 eventId 查询：
 * {@code GET /api/mas/tasks/{taskId}/events/{eventId}}
 */
@Data
@Builder
public class MasEventDTO {

    /** 事件唯一 ID（前端可用此 ID 查询完整 payload） */
    String eventId;

    /** 事件类型名称 */
    String type;

    /** 节点 ID（Node 层事件才有值） */
    String nodeId;

    /** Agent 角色（Node 层事件才有值，用于前端展示 Agent 名称） */
    String agentRole;

    /** 简短摘要（直接展示给用户） */
    String summary;

    /** 当前 Phase 编号 */
    int phaseIndex;

    LocalDateTime timestamp;
}
