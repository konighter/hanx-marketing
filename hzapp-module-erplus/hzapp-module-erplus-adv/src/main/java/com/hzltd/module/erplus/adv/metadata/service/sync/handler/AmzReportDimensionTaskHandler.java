package com.hzltd.module.erplus.adv.metadata.service.sync.handler;

import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.erplus.adv.adapter.service.AdsReportApiFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportBatchDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportBatchMapper;
import com.hzltd.module.erplus.adv.enums.AdsReportStatus;
import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.adv.service.AdsReportApi;
import com.hzltd.module.erplus.system.service.ErpTaskHandler;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 亚马逊广告维度报表任务处理器 (子任务)
 *
 * @author antigravity
 */
@Slf4j
@Component
public class AmzReportDimensionTaskHandler implements ErpTaskHandler {

    @Resource
    private AdsReportApiFactory adsReportApiFactory;

    @Resource
    private AdsReportBatchMapper adsReportBatchMapper;

    @Resource
    private SystemShopService systemShopService;

    @Override
    public ErpTaskResult onStart(ErpTaskDTO task) {
        log.info("[AmzReportDimensionTaskHandler] 开始提交报表任务: {}", task.getId());
        ShopModel shopModel = systemShopService.getShopByIdWithoutTenant(task.getShopId());

        ErpTaskResult result = TenantUtils.execute(shopModel.getTenantId().longValue(), () -> {
            return this.doStart(task, shopModel);
        });

        log.info("[AmzReportDimensionTaskHandler] 开始提交报表任务完成: {}, result = {}", task.getId(), result.getStatus());
        return result;

    }

    private ErpTaskResult doStart(ErpTaskDTO task, ShopModel shop) {
        AdsReportApi api = adsReportApiFactory.getAdsApiService(AdsPlatformEnum.valueOf(task.getPlatform()));
        AdsReportRequest req = buildRequest(task);
        req.setShopId(shop.getId());
        AdsResponse<String> response = api.submitAsyncReport(wrap(task, req));
        if (response.isSuccess()) {
            String platformJobId = response.getData();
            log.info("[AmzReportDimensionTaskHandler] 报表任务 {} 提交成功, platformJobId={}", task.getId(), platformJobId);
            return ErpTaskResult.submitted(platformJobId);
        } else {
            return ErpTaskResult.failed(response.getMessage());
        }
    }

    @Override
    public ErpTaskResult onPoll(ErpTaskDTO task) {
        log.info("[AmzReportDimensionTaskHandler] 轮询报表任务状态: {}", task.getId());

        ShopModel shopModel = systemShopService.getShopByIdWithoutTenant(task.getShopId());

        ErpTaskResult result = TenantUtils.execute(shopModel.getTenantId().longValue(), () -> {
            return this.doPoll(task, shopModel);
        });

        log.info("[AmzReportDimensionTaskHandler] 轮询报表任务状态完成: {}, result = {}", task.getId(), result.getStatus());
        return result;
    }

    private ErpTaskResult doPoll(ErpTaskDTO task, ShopModel shop) {

        AdsReportApi api = adsReportApiFactory.getAdsApiService(AdsPlatformEnum.valueOf(task.getPlatform()));
        AdsReportRequest req = buildRequest(task);

        AdsReportStatusRequest statusReq = new AdsReportStatusRequest();
        statusReq.setPlatformJobId(task.getPlatformJobId());
        statusReq.setReportRequest(req);

        AdsResponse<AdsReportStatus> statusResponse = api.getReportStatus(wrap(task, statusReq));
        if (!statusResponse.isSuccess()) {
            return ErpTaskResult.failed(statusResponse.getMessage());
        }

        AdsReportStatus status = statusResponse.getData();
        if (status == AdsReportStatus.COMPLETED) {
            log.info("[AmzReportDimensionTaskHandler] 报表任务 {} 平台生成完成，开始下载...", task.getId());
            AdsReportProcessRequest processReq = new AdsReportProcessRequest();
            processReq.setPlatformJobId(task.getPlatformJobId());
            processReq.setReportRequest(req);

            AdsResponse<Void> processResponse = api.downloadAndProcess(wrap(task, processReq), data -> {
                saveToDoris(task, (List<AdsReportBatchDO>) data);
            });

            if (processResponse.isSuccess()) {
                return ErpTaskResult.success();
            } else {
                return ErpTaskResult.failed(processResponse.getMessage());
            }
        } else if (status == AdsReportStatus.FAILED) {
            return ErpTaskResult.failed("Platform report generation failed");
        }

        // 仍在排队或执行中
        return ErpTaskResult.submitted(task.getPlatformJobId());
    }

    @Override
    public String getTaskType() {
        return AdsSyncTaskTypeEnum.REPORT_DIMENSION.getCode();
    }

    private AdsReportRequest buildRequest(ErpTaskDTO task) {
        return AdsReportRequest.builder()
                .shopId(task.getShopId())
                .platform(task.getPlatform())
                .startDate(task.getDateRangeStart())
                .endDate(task.getDateRangeEnd())
                .context(task.getContext())
                .build();
    }

    private void saveToDoris(ErpTaskDTO task, List<AdsReportBatchDO> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        for (AdsReportBatchDO item : dataList) {
            item.setShopId(task.getShopId().longValue());
            item.setPlatform(task.getPlatform());
            adsReportBatchMapper.insert(item);
        }
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
