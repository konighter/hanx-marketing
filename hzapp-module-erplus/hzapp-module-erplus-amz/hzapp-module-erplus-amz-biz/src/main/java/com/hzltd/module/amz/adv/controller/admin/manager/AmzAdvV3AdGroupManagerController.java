package com.hzltd.module.amz.adv.controller.admin.manager;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.*;
import com.hzltd.module.amz.adv.service.AdsAmazonGroupMrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台 - 亚马逊广告组V3管理器")
@RestController
@RequestMapping("/erplus/amz/adv/v3/ad-group/manager")
@Validated
public class AmzAdvV3AdGroupManagerController {

    @Resource
    private AdsAmazonGroupMrgService amazonGroupMrgService;

    @PostMapping("/targeting/batch-create")
    @Operation(summary = "定向-批量创建")
    public CommonResult<Boolean> batchCreateTargeting(@Valid @RequestBody AmzAdvV3TargetingBatchCreateReqVO reqVO) {
        return amazonGroupMrgService.batchCreateTargeting(reqVO);
    }

    @PutMapping("/targeting/batch-update-bid")
    @Operation(summary = "定向-批量修改出价")
    public CommonResult<Boolean> batchUpdateTargetingBid(@Valid @RequestBody AmzAdvV3TargetingBatchUpdateBidReqVO reqVO) {
        return amazonGroupMrgService.batchUpdateTargetingBid(reqVO);
    }

    @DeleteMapping("/targeting/batch-delete")
    @Operation(summary = "定向-批量删除")
    public CommonResult<Boolean> batchDeleteTargeting(@Valid @RequestBody AmzAdvV3IdListReqVO reqVO) {
        return amazonGroupMrgService.batchDeleteTargeting(reqVO);
    }

    // --- Negative Targeting ---

    @PostMapping("/negative-targeting/batch-create")
    @Operation(summary = "否定定向-批量创建")
    public CommonResult<Boolean> batchCreateNegativeTargeting(@Valid @RequestBody AmzAdvV3NegativeTargetingBatchCreateReqVO reqVO) {
        return amazonGroupMrgService.batchCreateNegativeTargeting(reqVO);
    }

    @DeleteMapping("/negative-targeting/batch-delete")
    @Operation(summary = "否定定向-批量删除")
    public CommonResult<Boolean> batchDeleteNegativeTargeting(@Valid @RequestBody AmzAdvV3IdListReqVO reqVO) {
        return amazonGroupMrgService.batchDeleteNegativeTargeting(reqVO);
    }

    // --- Keyword ---

    @PostMapping("/keyword/batch-create")
    @Operation(summary = "关键词-批量创建")
    public CommonResult<Boolean> batchCreateKeyword(@Valid @RequestBody AmzAdvV3KeywordBatchCreateReqVO reqVO) {
        return amazonGroupMrgService.batchCreateKeyword(reqVO);
    }

    @PutMapping("/keyword/batch-update-bid")
    @Operation(summary = "关键词-批量修改出价")
    public CommonResult<Boolean> batchUpdateKeywordBid(@Valid @RequestBody AmzAdvV3KeywordBatchUpdateBidReqVO reqVO) {
        return amazonGroupMrgService.batchUpdateKeywordBid(reqVO);
    }

    @DeleteMapping("/keyword/batch-delete")
    @Operation(summary = "关键词-批量删除")
    public CommonResult<Boolean> batchDeleteKeyword(@Valid @RequestBody AmzAdvV3IdListReqVO reqVO) {
        return amazonGroupMrgService.batchDeleteKeyword(reqVO);
    }

    // --- Negative Keyword ---

    @PostMapping("/negative-keyword/batch-create")
    @Operation(summary = "否定关键词-批量创建")
    public CommonResult<Boolean> batchCreateNegativeKeyword(@Valid @RequestBody AmzAdvV3NegativeKeywordBatchCreateReqVO reqVO) {
        return amazonGroupMrgService.batchCreateNegativeKeyword(reqVO);
    }

    @DeleteMapping("/negative-keyword/batch-delete")
    @Operation(summary = "否定关键词-批量删除")
    public CommonResult<Boolean> batchDeleteNegativeKeyword(@Valid @RequestBody AmzAdvV3IdListReqVO reqVO) {
        return amazonGroupMrgService.batchDeleteNegativeKeyword(reqVO);
    }
}

