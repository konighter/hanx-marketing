package com.hzltd.module.amz.spapi.job.handler;

import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.service.TenantBaseTaskHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 亚马逊品牌分析 - 父任务处理器
 * 负责将任务拆解为具体的报表子任务
 */
@Slf4j
@Component
public class AmzDataReportParentHandler extends TenantBaseTaskHandler {

    @Resource
    private ErpTaskEngine taskEngine;

    private static final List<String> REPORT_TYPES = Arrays.asList(
            "GET_BRAND_ANALYTICS_SEARCH_TERMS_REPORT",
            "GET_BRAND_ANALYTICS_MARKET_BASKET_REPORT",
            "GET_BRAND_ANALYTICS_SEARCH_CATALOG_PERFORMANCE_REPORT",
            "GET_BRAND_ANALYTICS_SEARCH_QUERY_PERFORMANCE_REPORT"
    );

    @Override
    public ErpTaskResult doStart(ErpTaskDTO task, ShopModel shop) {
        log.info("[AmzDataReportParentHandler] 开始拆分子任务, 店铺: {}", shop.getId());

        for (String type : REPORT_TYPES) {
            Map<String, Object> context = new HashMap<>();
            context.put("reportType", type);

            // 子任务的 UniqueId 包含报告类型，确保幂等
            String uniqueId = "AMZ_DATA_CHILD_" + shop.getId() + "_" + type + "_" + task.getDateRangeStart();

            ErpTaskSubmitRequest childRequest = ErpTaskSubmitRequest.builder()
                    .shopId(shop.getId())
                    .platform("AMAZON")
                    .taskType(AdsSyncTaskTypeEnum.AMZ_DATA_REPORT_CHILD.getCode())
                    .taskUniqueId(uniqueId)
                    .dateRangeStart(task.getDateRangeStart())
                    .dateRangeEnd(task.getDateRangeEnd())
                    .context(context)
                    .maxRetries(3)
                    .build();

            taskEngine.submitTask(childRequest);
            log.info("[AmzDataReportParentHandler] 已提交子任务: {}", type);
        }

        // 父任务直接返回成功，子任务会由引擎独立调度
        return ErpTaskResult.success();
    }

    @Override
    public ErpTaskResult doPoll(ErpTaskDTO task, ShopModel shop) {
        return ErpTaskResult.success();
    }

    @Override
    public String getTaskType() {
        return AdsSyncTaskTypeEnum.AMZ_DATA_REPORT_PARENT.getCode();
    }
}
