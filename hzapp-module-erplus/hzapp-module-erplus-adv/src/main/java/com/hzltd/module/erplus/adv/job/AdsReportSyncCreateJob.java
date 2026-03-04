package com.hzltd.module.erplus.adv.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.metadata.service.sync.AdsPerformanceSyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdsReportSyncCreateJob implements JobHandler {
    @Resource
    private AdsPerformanceSyncService performanceSyncService;


    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        log.debug("[AdsReportSyncCreateJob] 开始创建每日同步任务...");
        try {
            performanceSyncService.createDailySyncTasks();
        } catch (Exception e) {
            log.error("[AdsReportSyncCreateJob] 创建每日同步任务异常", e);
        }
        return "";
    }
}
