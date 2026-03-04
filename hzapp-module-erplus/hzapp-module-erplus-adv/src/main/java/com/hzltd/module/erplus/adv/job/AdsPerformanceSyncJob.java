package com.hzltd.module.erplus.adv.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.metadata.service.sync.AdsPerformanceSyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 广告数据同步定时任务
 */
@Slf4j
@Component
public class AdsPerformanceSyncJob implements JobHandler {

    @Resource
    private AdsPerformanceSyncService performanceSyncService;


    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        log.debug("[AdsSyncJob] 开始轮询异步报表状态...");
        try {
            performanceSyncService.processActiveReportTasks();
        } catch (Exception e) {
            log.error("[AdsSyncJob] 轮询报表状态异常", e);
        }
        return "";
    }

}
