package com.hzltd.module.erplus.ai.masv0.framework.event;

import lombok.Getter;

/**
 * 用户实时反馈事件
 */
@Getter
public class UserFeedbackEvent extends MasEvent.BaseEvent {
    
    private final String feedback;

    public UserFeedbackEvent(String sessionId, String feedback) {
        super(sessionId);
        this.feedback = feedback;
    }
}
