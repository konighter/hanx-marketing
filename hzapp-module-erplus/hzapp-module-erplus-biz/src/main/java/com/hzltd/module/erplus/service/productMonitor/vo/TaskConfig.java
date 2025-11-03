package com.hzltd.module.erplus.service.productMonitor.vo;

import lombok.Data;

@Data
public class TaskConfig {

    public static final TaskConfig DEFAULT_TASK_CONFIG = new TaskConfig();
    static {
//        DEFAULT_TASK_CONFIG.setTaskName("default_task");
    }

    private Boolean async = false;
    private Integer threadCount = 1;
    private Integer maxTaskWait = 100;
    private Integer timeout = 10000; // ms: 10s
    private Integer maxRetryTime = 3;
    private Integer delay = 10000; // ms: 10
}
