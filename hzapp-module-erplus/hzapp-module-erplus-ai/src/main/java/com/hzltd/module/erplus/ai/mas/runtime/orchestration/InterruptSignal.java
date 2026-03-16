package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

/**
 * URGENT 中断信号载体.
 */
public record InterruptSignal(String sessionId, String message, boolean isUrgent) {
}
