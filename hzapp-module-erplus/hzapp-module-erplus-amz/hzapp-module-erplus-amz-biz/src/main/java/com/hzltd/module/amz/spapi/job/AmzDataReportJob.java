package com.hzltd.module.amz.spapi.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * 亚马逊品牌分析同步 Job (父任务触发器)
 */
@Slf4j
@Component
public class AmzDataReportJob implements JobHandler {

    @Resource
    private ErpTaskEngine taskEngine;

    @Resource
    private SystemShopService shopService;

    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        log.info("[AmzDataReportJob] 开始调度亚马逊品牌分析父任务...");
        
        List<ShopModel> shops = shopService.getAvailableShops();
        
        LocalDate today = LocalDate.now();
        LocalDate lastSunday = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate start = lastSunday.minusWeeks(1);
        LocalDate end = lastSunday.minusDays(1);

        for (ShopModel shop : shops) {
            String uniqueId = "AMZ_DATA_PARENT_" + shop.getId() + "_" + start.toString();
            
            ErpTaskSubmitRequest request = ErpTaskSubmitRequest.builder()
                    .shopId(shop.getId())
                    .platform("AMAZON")
                    .taskType(AdsSyncTaskTypeEnum.AMZ_DATA_REPORT_PARENT.getCode())
                    .taskUniqueId(uniqueId)
                    .dateRangeStart(start)
                    .dateRangeEnd(end)
                    .maxRetries(3)
                    .build();
            
            taskEngine.submitTask(request);
            log.info("[AmzDataReportJob] 店铺 {} ({}) 已提交父任务", shop.getName(), shop.getId());
        }

        return "SUCCESS";
    }
}
