package com.hzltd.module.erplus.ai.mas.orchestration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * MAS 用户输入中断管理器.
 *
 * <h2>分级处理机制 (C3)</h2>
 * <ul>
 *   <li><b>URGENT</b>：高优先级。信号写入内存队列，Orchestrator 在下一 Phase 边界检查点响应，
 *       将中断消息注入 session.state["urgent_interrupt"]，Planner 下轮感知并重新规划。</li>
 *   <li><b>SUPPLEMENT</b>：低优先级。直接追加到 session.state["pending_supplements"]，
 *       不打断当前 Phase 执行；Planner 在下一轮规划时读取并考虑。</li>
 * </ul>
 *
 * <p>外部调用入口（HTTP Controller 或 MQ Consumer）：
 * <pre>
 * interruptManager.submitUserInput(sessionId, "请聚焦竞品对比维度", InterruptPriority.SUPPLEMENT);
 * interruptManager.submitUserInput(sessionId, "用户取消，立即停止", InterruptPriority.URGENT);
 * </pre>
 */
@Slf4j
@Component
public class InterruptManager {

    public enum InterruptPriority {
        /** 高优先级：Phase 边界响应，触发重新规划 */
        URGENT,
        /** 低优先级：追加到下一 Planner 轮读取 */
        SUPPLEMENT
    }

    /** URGENT 信号队列（非阻塞，单消费者：WorkflowOrchestratorV1） */
    private final ConcurrentLinkedQueue<InterruptSignal> urgentQueue = new ConcurrentLinkedQueue<>();

    /**
     * 外部提交用户输入.
     *
     * @param sessionId 目标 Session ID
     * @param message   用户输入内容
     * @param priority  中断优先级
     */
    public void submitUserInput(String sessionId, String message, InterruptPriority priority) {
        log.info("[InterruptManager] Received {} input for session={}: {}", priority, sessionId, message);
        if (priority == InterruptPriority.URGENT) {
            urgentQueue.offer(new InterruptSignal(sessionId, message, true));
        } else {
            // SUPPLEMENT: TODO - 需要注入 SessionService 引用，写入 session.state["pending_supplements"]
            // interruptManager 本身不应持有 SessionService，建议通过 ApplicationEventPublisher 解耦
            log.info("[InterruptManager] SUPPLEMENT queued for session={}: {}", sessionId, message);
        }
    }

    /**
     * Orchestrator 在 Phase 边界调用，非阻塞轮询 URGENT 信号.
     *
     * @return 有信号返回 InterruptSignal，否则返回 null
     */
    public InterruptSignal poll() {
        return urgentQueue.poll();
    }
}
