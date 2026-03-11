package com.hzltd.module.erplus.ai.masv0.framework.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MAS 事件基类
 */
public interface MasEvent {
    
    /**
     * 获取事件唯一标识
     */
    String getEventId();

    /**
     * 获取事件发生时间
     */
    LocalDateTime getTimestamp();

    /**
     * 获取会话标识 (关联一组相关的 Agent 交互)
     */
    String getSessionId();
    
    /**
     * 默认实现
     */
    abstract class BaseEvent implements MasEvent {
        private final String eventId = UUID.randomUUID().toString();
        private final LocalDateTime timestamp = LocalDateTime.now();
        private final String sessionId;

        protected BaseEvent(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public String getEventId() { return eventId; }

        @Override
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String getSessionId() { return sessionId; }
    }
}
