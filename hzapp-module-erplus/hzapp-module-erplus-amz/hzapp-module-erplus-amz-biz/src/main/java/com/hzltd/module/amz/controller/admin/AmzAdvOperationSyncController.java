package com.hzltd.module.amz.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.service.AmzAdvOperationIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告运营(同步)")
@RestController
@RequestMapping("/erplus/amz-adv-operation-sync")
public class AmzAdvOperationSyncController {

    @Resource
    private AmzAdvOperationIntegrationService amzAdvOperationIntegrationService;

    // ========== 同步广告活动管理 ==========
    
    @PostMapping("/campaign/create-sync")
    @Operation(summary = "创建广告活动(同步到亚马逊)")
    public CommonResult<Long> createCampaignSync(
            @RequestParam("shopId") String shopId,
            @RequestBody @Valid AmzAdvCampaignSaveReqVO createReqVO) {
        return success(amzAdvOperationIntegrationService.createCampaignSync(shopId, createReqVO));
    }

    @PostMapping("/campaign/sync-to-amazon/{id}")
    @Operation(summary = "同步广告活动到亚马逊")
    public CommonResult<Boolean> syncCampaignToAmazon(
            @RequestParam("shopId") String shopId,
            @PathVariable("id") Long id) {
        amzAdvOperationIntegrationService.syncCampaignToAmazon(shopId, id);
        return success(true);
    }

    @GetMapping("/campaign/performance/{id}")
    @Operation(summary = "获取广告活动表现数据")
    public CommonResult<Object> getCampaignPerformance(
            @RequestParam("shopId") String shopId,
            @PathVariable("id") Long id) {
        Object performance = amzAdvOperationIntegrationService.getCampaignPerformance(shopId, id);
        return success(performance);
    }

    @GetMapping("/campaign/list-with-sync-status")
    @Operation(summary = "获取广告活动列表(含同步状态)")
    public CommonResult<PageResult<AmzAdvCampaignDO>> getCampaignsWithSyncStatus(
            AmzAdvCampaignPageReqVO pageReqVO) {
        return success(amzAdvOperationIntegrationService.getCampaignsWithSyncStatus(pageReqVO));
    }
}