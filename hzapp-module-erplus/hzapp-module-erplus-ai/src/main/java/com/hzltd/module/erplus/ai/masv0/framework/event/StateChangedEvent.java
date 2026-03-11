package com.hzltd.module.erplus.ai.masv0.framework.event;

import com.hzltd.module.erplus.ai.masv0.framework.state.MasState;
import lombok.Getter;

/**
 * 状态变更事件
 */
@Getter
public class StateChangedEvent extends MasEvent.BaseEvent {
    
    private final MasState from;
    private final MasState to;

    public StateChangedEvent(String sessionId, MasState from, MasState to) {
        super(sessionId);
        this.from = from;
        this.to = to;
    }
}
