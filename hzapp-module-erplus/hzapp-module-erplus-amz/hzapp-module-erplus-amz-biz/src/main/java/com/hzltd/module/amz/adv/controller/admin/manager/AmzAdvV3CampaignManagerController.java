package com.hzltd.module.amz.adv.controller.admin.manager;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.*;
import com.hzltd.module.amz.adv.service.AdsAmazonCampaignMrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台 - 亚马逊广告活动V3管理器")
@RestController
@RequestMapping("/erplus/amz/adv/v3/campaign/manager")
@Validated
public class AmzAdvV3CampaignManagerController {

    @Resource
    private AdsAmazonCampaignMrgService amazonCampaignMrgService;

    @PutMapping("/dynamic-bidding/update")
    @Operation(summary = "动态竞价-统一更新 (策略/位置/受众)")
    public CommonResult<Boolean> updateDynamicBidding(@Valid @RequestBody AmzAdvV3CampaignDynamicBiddingUpdateReqVO reqVO) {
        return amazonCampaignMrgService.updateDynamicBidding(reqVO);
    }

    @PostMapping("/negative-keyword/batch-create")
    @Operation(summary = "否定关键词-批量创建")
    public CommonResult<Boolean> batchCreateNegativeKeyword(@Valid @RequestBody AmzAdvV3CampaignNegativeKeywordBatchCreateReqVO reqVO) {
        return amazonCampaignMrgService.batchCreateNegativeKeyword(reqVO);
    }

    @DeleteMapping("/negative-keyword/batch-delete")
    @Operation(summary = "否定关键词-批量删除")
    public CommonResult<Boolean> batchDeleteNegativeKeyword(@Valid @RequestBody AmzAdvV3CampaignNegativeKeywordBatchDeleteReqVO reqVO) {
        return amazonCampaignMrgService.batchDeleteNegativeKeyword(reqVO);
    }

    @PostMapping("/negative-targeting/batch-create")
    @Operation(summary = "否定定向-批量创建")
    public CommonResult<Boolean> batchCreateNegativeTargeting(@Valid @RequestBody AmzAdvV3CampaignNegativeTargetingBatchCreateReqVO reqVO) {
        return amazonCampaignMrgService.batchCreateNegativeTargeting(reqVO);
    }

    @DeleteMapping("/negative-targeting/batch-delete")
    @Operation(summary = "否定定向-批量删除")
    public CommonResult<Boolean> batchDeleteNegativeTargeting(@Valid @RequestBody AmzAdvV3CampaignNegativeTargetingBatchDeleteReqVO reqVO) {
        return amazonCampaignMrgService.batchDeleteNegativeTargeting(reqVO);
    }
}
