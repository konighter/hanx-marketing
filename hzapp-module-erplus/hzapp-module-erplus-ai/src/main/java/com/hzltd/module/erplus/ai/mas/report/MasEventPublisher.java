package com.hzltd.module.erplus.ai.mas.report;

import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * MAS 事件双轨派发入口.
 * <p>
 * 负责将一个 {@link MasEvent} 同时分发到：
 * <ol>
 *   <li>{@link MasEventLogService} — 持久化到数据库，用于审计和历史回放</li>
 *   <li>{@link MasReporter} — 实时推送给前端客户端（WebSocket）</li>
 * </ol>
 * <p>
 * 这是 Orchestrator / Dispatcher 层发布事件的<b>唯一入口</b>，ADK Callbacks 中调用此类。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MasEventPublisher {

    private final MasEventLogService eventLogService;
    private final MasReporter reporter;

    /**
     * 发布 MAS 执行事件，同时写日志 + 推流.
     *
     * @param event 执行事件
     */
    public void publish(MasEvent event) {
        // 1. 持久化事件日志（用于审计、历史回放、客户端重连恢复）
        try {
            eventLogService.logEvent(
                    event.getSessionId(),
                    event.getNodeId() != null ? event.getNodeId() : "ORCHESTRATOR",
                    event.getType().name(),
                    event.getSummary(),
                    event.getPayload()
            );
        } catch (Exception e) {
            log.error("[MasEventPublisher] Failed to persist event: type={}, session={}", 
                      event.getType(), event.getSessionId(), e);
        }

        // 2. 实时推流（非阻塞，失败不影响执行流程）
        try {
            reporter.push(event);
        } catch (Exception e) {
            log.warn("[MasEventPublisher] Failed to push event to reporter: type={}, session={}",
                     event.getType(), event.getSessionId(), e);
        }
    }
}
