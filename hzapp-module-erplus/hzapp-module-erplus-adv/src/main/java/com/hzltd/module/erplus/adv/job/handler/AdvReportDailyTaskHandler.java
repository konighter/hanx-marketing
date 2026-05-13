package com.hzltd.module.erplus.adv.job.handler;

import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.erplus.adv.adapter.service.AdsReportApiFactory;
import com.hzltd.module.erplus.system.enums.ScheduleTaskTypeEnum;
import com.hzltd.module.erplus.adv.model.AdsReportGetRequest;
import com.hzltd.module.erplus.adv.model.AdsReportRequest;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import com.hzltd.module.erplus.adv.service.AdsReportApi;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.service.ErpTaskHandler;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 亚马逊广告每日同步任务处理器 (父任务)
 *
 * @author antigravity
 */
@Slf4j
@Component
public class AdvReportDailyTaskHandler implements ErpTaskHandler {

    @Resource
    private AdsReportApiFactory adsReportApiFactory;

    @Resource
    private ErpTaskEngine taskEngine;

    @Resource
    private SystemShopService systemShopService;

    @Override
    public ErpTaskResult onStart(ErpTaskDTO task) {
        log.info("[AmzReportDailyTaskHandler] 开始拆分每日同步任务: {}", task.getId());
        ShopModel shop = systemShopService.getShopByIdWithoutTenant(task.getShopId());
        return TenantUtils.execute(shop.getTenantId().longValue(), () -> doStart(task, shop));
    }

    private ErpTaskResult doStart(ErpTaskDTO task, ShopModel shop) {
        AdsReportApi api = adsReportApiFactory.getAdsApiService(AdsPlatformEnum.valueOf(task.getPlatform()));
        if (api == null) {
            return ErpTaskResult.failed("No API service for platform: " + task.getPlatform());
        }

        AdsReportGetRequest getReq = new AdsReportGetRequest();
        getReq.setStartDate(task.getDateRangeStart());
        getReq.setEndDate(task.getDateRangeEnd());

        AdsResponse<List<AdsReportRequest>> response = api.getReportRequests(wrap(task, getReq));

        if (!response.isSuccess()) {
            return ErpTaskResult.failed(response.getMessage());
        }

        if (response.getData() == null || response.getData().isEmpty()) {
            return ErpTaskResult.success();
        }

        // 提交子任务
        List<ErpTaskSubmitRequest> childRequests = response.getData().stream()
                .map(req -> ErpTaskSubmitRequest.builder()
                        .shopId(task.getShopId())
                        .platform(task.getPlatform())
                        .taskType(ScheduleTaskTypeEnum.REPORT_DIMENSION.getCode())
                        .taskUniqueId(generateUniqueId(task, req))
                        .dateRangeStart(req.getStartDate())
                        .dateRangeEnd(req.getEndDate())
                        .context(req.getContext())
                        .maxRetries(3)
                        .build())
                .collect(Collectors.toList());

        taskEngine.submitChildTasks(task.getId(), childRequests);

        return ErpTaskResult.success();
    }

    @Override
    public ErpTaskResult onPoll(ErpTaskDTO task) {
        // 父任务不需要轮询，子任务完成后引擎会自动处理
        return null;
    }

    @Override
    public String getTaskType() {
        return ScheduleTaskTypeEnum.REPORT_DAILY.getCode();
    }

    private String generateUniqueId(ErpTaskDTO parent, AdsReportRequest req) {
        // 基于父任务ID、报表类型和日期生成唯一标识
        String reportType = (String) req.getContext().get("reportType");
        return parent.getId() + "_" + reportType + "_" + req.getStartDate();
    }

    private <T> AdsRequest<T> wrap(ErpTaskDTO task, T request) {
        return AdsRequest.<T>builder()
                .shopId(task.getShopId().longValue())
                .platform(AdsPlatformEnum.valueOf(task.getPlatform()))
                .timestamp(System.currentTimeMillis())
                .request(request)
                .build();
    }
}
