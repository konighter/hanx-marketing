package com.hzltd.module.erplus.ai.mas.runtime.report;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 组合多个 MasReporter 的复合实现.
 * <p>
 * 支持同时将事件推送到多个目标（如 WebSocket + 钉钉机器人 + 企业微信），
 * 任一 Reporter 失败不影响其他 Reporter 的执行。
 * <p>
 * 使用示例：
 * <pre>
 * {@literal @}Bean
 * public MasReporter masReporter(WebSocketMasReporter wsReporter, DingTalkReporter dingReporter) {
 *     return new CompositeReporter(List.of(wsReporter, dingReporter));
 * }
 * </pre>
 */
@Slf4j
public class CompositeReporter implements MasReporter {

    private final List<MasReporter> reporters;

    public CompositeReporter(List<MasReporter> reporters) {
        this.reporters = reporters;
    }

    @Override
    public void push(MasEvent event) {
        for (MasReporter reporter : reporters) {
            try {
                reporter.push(event);
            } catch (Exception e) {
                log.warn("[CompositeReporter] Reporter {} failed: {}", 
                         reporter.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
