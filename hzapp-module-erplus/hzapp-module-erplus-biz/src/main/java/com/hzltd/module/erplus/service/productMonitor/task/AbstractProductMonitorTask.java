package com.hzltd.module.erplus.service.productMonitor.task;

import com.hzltd.module.erplus.service.productMonitor.vo.MonitorMetricsVO;
import com.hzltd.module.erplus.service.productMonitor.vo.TaskConfig;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Getter
public abstract class AbstractProductMonitorTask {

    private TaskConfig taskConfig;

    public void run() {
        run(TaskConfig.DEFAULT_TASK_CONFIG);
    }


    public void run(TaskConfig taskConfig) {
        // 初始化
        init(taskConfig);
        List<MonitorMetricsVO> metrics = doCrawl();
        saveMetrics(metrics);
    }

    public void init(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public ExecutorService getExecutorService() {
        return null;
    }

    public abstract String getTaskName();



    protected List<MonitorMetricsVO> doCrawl() {
        return null;
    }

    protected void saveMetrics(List<MonitorMetricsVO> metrics) {
        // 保存指标
    }





}
