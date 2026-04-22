package com.hzltd.module.erplus.adv.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.metadata.service.sync.AdsSyncTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 广告异步任务统一调度 Job
 * 
 * 职责：
 * 1. 触发任务创建 (基于时区)
 * 2. 触发任务处理与状态轮询
 */
@Slf4j
@Component
public class AdsSyncTaskJob implements JobHandler {

    @Resource
    private AdsSyncTaskService adsSyncTaskService;

    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        log.info("[AdsSyncTaskJob] 开始执行同步任务调度...");
        
        // 1. 创建昨日任务 (按各店铺时区判断)
        try {
            adsSyncTaskService.createDailyTasks();
        } catch (Exception e) {
            log.error("[AdsSyncTaskJob] 创建每日任务异常", e);
        }
        
        // 2. 处理活跃任务 (拆分、提交、轮询)
        try {
            adsSyncTaskService.processActiveTasks();
        } catch (Exception e) {
            log.error("[AdsSyncTaskJob] 处理活跃任务异常", e);
        }
        
        return "SUCCESS";
    }

}
