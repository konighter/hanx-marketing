package com.hzltd.module.erplus.ai.mas.framework.event;

import com.hzltd.module.erplus.ai.mas.framework.execution.MasTask;
import lombok.Getter;

/**
 * 任务执行结果反馈事件
 */
@Getter
public class TaskFinishedEvent extends MasEvent.BaseEvent {
    
    private final MasTask task;
    private final String result;
    private final Status status;
    private final long executionTime;

    public enum Status {
        SUCCESS, FAILED, INTERRUPTED
    }

    public TaskFinishedEvent(String sessionId, MasTask task, String result, Status status, long executionTime) {
        super(sessionId);
        this.task = task;
        this.result = result;
        this.status = status;
        this.executionTime = executionTime;
    }
}
