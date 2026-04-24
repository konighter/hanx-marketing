package com.hzltd.module.erplus.sys.service.task;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 通用任务引擎调度 Job
 *
 * @author antigravity
 */
@Component
public class ErpTaskScheduleJob implements JobHandler {

    @Resource
    private ErpTaskEngineImpl taskEngine;

    @Override
    public String execute(String param) throws Exception {
        taskEngine.schedule();
        return "success";
    }
}
