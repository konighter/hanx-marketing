package com.hzltd.module.erplus.adv.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

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
    private ErpTaskEngine taskEngine;

    @Resource
    private SystemShopService shopService;

    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        log.info("[AdsSyncTaskJob] 开始执行广告报表同步任务调度...");
        
        // 1. 创建昨日任务 (按各店铺时区判断)
        try {
            createDailyTasks();
        } catch (Exception e) {
            log.error("[AdsSyncTaskJob] 创建广告报表同步任务异常", e);
        }
        log.info("[AdsSyncTaskJob] 广告报表任务调度成功");
        return "SUCCESS";
    }

    @Transactional(rollbackFor = Exception.class)
    public void createDailyTasks() {
        // 1. 获取所有活跃店铺
        List<ShopModel> shops = shopService.getAvailableShops();

        for (ShopModel shop : shops) {
            AdsPlatformEnum platform = AdsPlatformEnum.of(CrossPlatformEnum.valueOf(shop.getPlatform()).getCode());
            if (platform == null) {
                continue;
            }

            // 2. 计算当地时区时间
            ZoneId zoneId = ZoneId.of(shop.getTimezone() != null ? shop.getTimezone() : "UTC");
            ZonedDateTime localNow = ZonedDateTime.now(zoneId);
            LocalDate today = localNow.toLocalDate();

            // 3. 计算同步范围：昨日 (T-1) 往前推 7 天
            LocalDate end = today.minusDays(1);
            LocalDate start = end.minusDays(7);

            // 4. 计算执行时机（作为当天任务的唯一标识）：当地时间凌晨 4:00
            long scheduledAt = today.atTime(4, 0).atZone(zoneId).toInstant().toEpochMilli();

            // 5. 提交到任务引擎 (引擎会处理幂等)
            ErpTaskSubmitRequest submitReq = ErpTaskSubmitRequest.builder()
                    .shopId(shop.getId())
                    .platform(platform.getCode())
                    .taskType(AdsSyncTaskTypeEnum.REPORT_DAILY.getCode())
                    .taskUniqueId("REPORT_DAILY_" + shop.getId() + "_" + scheduledAt)
                    .dateRangeStart(start)
                    .dateRangeEnd(end)
                    .scheduledAt(scheduledAt)
                    .maxRetries(3)
                    .build();

            taskEngine.submitTask(submitReq);
            log.info("[createDailyTasks] 为店铺 {} ({}) 提交同步任务到引擎: 预计执行时间 {}, 数据范围 {} ~ {}",
                    shop.getName(), shop.getId(), LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(scheduledAt), zoneId), start, end);
        }
    }


}
