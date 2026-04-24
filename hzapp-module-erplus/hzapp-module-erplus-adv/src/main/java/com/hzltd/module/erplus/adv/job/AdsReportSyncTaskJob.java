package com.hzltd.module.erplus.adv.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.metadata.service.sync.AdsReportSyncTaskService;
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
public class AdsReportSyncTaskJob implements JobHandler {

    @Resource
    private AdsReportSyncTaskService adsReportSyncTaskService;

    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        log.info("[AdsSyncTaskJob] 开始执行广告报表同步任务调度...");
        
        // 1. 创建昨日任务 (按各店铺时区判断)
        try {
            adsReportSyncTaskService.createDailyTasks();
        } catch (Exception e) {
            log.error("[AdsSyncTaskJob] 创建广告报表同步任务异常", e);
        }
        log.info("[AdsSyncTaskJob] 广告报表任务调度成功");
        return "SUCCESS";
    }

}
