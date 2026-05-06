package com.hzltd.module.erplus.adv.metadata.service.sync;

import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 广告同步任务核心服务实现类
 */
@Slf4j
@Service
public class AdsReportSyncTaskServiceImpl implements AdsReportSyncTaskService {

    @Resource
    private ErpTaskEngine taskEngine;

    @Resource
    private SystemShopService shopService;


    @Override
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

    @Override
    public void processActiveTasks() {
        // 逻辑已迁移至 ErpTaskEngine 调度
    }


}
