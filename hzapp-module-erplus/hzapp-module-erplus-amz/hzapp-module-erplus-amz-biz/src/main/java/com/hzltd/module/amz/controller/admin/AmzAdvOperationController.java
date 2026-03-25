package com.hzltd.module.amz.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.*;
import com.hzltd.module.amz.dal.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.amz.service.AmzAdvAdGroupService;
import com.hzltd.module.amz.service.AmzAdvBidStrategyService;
import com.hzltd.module.amz.service.AmzAdvCampaignService;
import com.hzltd.module.amz.service.AmzAdvKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告运营")
@RestController
@RequestMapping("/erplus/amz-adv-operation")
public class AmzAdvOperationController {

    @Resource
    private AmzAdvCampaignService amzAdvCampaignService;
    @Resource
    private AmzAdvAdGroupService amzAdvAdGroupService;
    @Resource
    private AmzAdvKeywordService amzAdvKeywordService;
    @Resource
    private AmzAdvBidStrategyService amzAdvBidStrategyService;

    // ========== 广告活动管理 ==========
    
    @PostMapping("/campaign/create")
    @Operation(summary = "创建广告活动")
    public CommonResult<Long> createCampaign(@RequestBody @Valid AmzAdvCampaignSaveReqVO createReqVO) {
        return success(amzAdvCampaignService.createAmzAdvCampaign(createReqVO));
    }

    @PutMapping("/campaign/update")
    @Operation(summary = "更新广告活动")
    public CommonResult<Boolean> updateCampaign(@RequestBody @Valid AmzAdvCampaignSaveReqVO updateReqVO) {
        amzAdvCampaignService.updateAmzAdvCampaign(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/campaign/delete/{id}")
    @Operation(summary = "删除广告活动")
    public CommonResult<Boolean> deleteCampaign(@PathVariable("id") Long id) {
        amzAdvCampaignService.deleteAmzAdvCampaign(id);
        return success(true);
    }

    @GetMapping("/campaign/get/{id}")
    @Operation(summary = "获取广告活动")
    public CommonResult<AmzAdvCampaignDO> getCampaign(@PathVariable("id") Long id) {
        return success(amzAdvCampaignService.getAmzAdvCampaign(id));
    }

    @GetMapping("/campaign/page")
    @Operation(summary = "分页获取广告活动")
    public CommonResult<PageResult<AmzAdvCampaignDO>> getCampaignPage(@Valid AmzAdvCampaignPageReqVO pageReqVO) {
        return success(amzAdvCampaignService.getAmzAdvCampaignPage(pageReqVO));
    }

    // ========== 广告组管理 ==========
    
    @PostMapping("/ad-group/create")
    @Operation(summary = "创建广告组")
    public CommonResult<Long> createAdGroup(@RequestBody @Valid AmzAdvAdGroupSaveReqVO createReqVO) {
        return success(amzAdvAdGroupService.createAmzAdvAdGroup(createReqVO));
    }

    @PutMapping("/ad-group/update")
    @Operation(summary = "更新广告组")
    public CommonResult<Boolean> updateAdGroup(@RequestBody @Valid AmzAdvAdGroupSaveReqVO updateReqVO) {
        amzAdvAdGroupService.updateAmzAdvAdGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/ad-group/delete/{id}")
    @Operation(summary = "删除广告组")
    public CommonResult<Boolean> deleteAdGroup(@PathVariable("id") Long id) {
        amzAdvAdGroupService.deleteAmzAdvAdGroup(id);
        return success(true);
    }

    @GetMapping("/ad-group/get/{id}")
    @Operation(summary = "获取广告组")
    public CommonResult<AmzAdvAdGroupDO> getAdGroup(@PathVariable("id") Long id) {
        return success(amzAdvAdGroupService.getAmzAdvAdGroup(id));
    }

    @GetMapping("/ad-group/page")
    @Operation(summary = "分页获取广告组")
    public CommonResult<PageResult<AmzAdvAdGroupDO>> getAdGroupPage(@Valid AmzAdvAdGroupPageReqVO pageReqVO) {
        return success(amzAdvAdGroupService.getAmzAdvAdGroupPage(pageReqVO));
    }

    // ========== 关键词管理 ==========
    
    @PostMapping("/keyword/create")
    @Operation(summary = "创建关键词")
    public CommonResult<Long> createKeyword(@RequestBody @Valid AmzAdvKeywordSaveReqVO createReqVO) {
        return success(amzAdvKeywordService.createAmzAdvKeyword(createReqVO));
    }

    @PutMapping("/keyword/update")
    @Operation(summary = "更新关键词")
    public CommonResult<Boolean> updateKeyword(@RequestBody @Valid AmzAdvKeywordSaveReqVO updateReqVO) {
        amzAdvKeywordService.updateAmzAdvKeyword(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/keyword/delete/{id}")
    @Operation(summary = "删除关键词")
    public CommonResult<Boolean> deleteKeyword(@PathVariable("id") Long id) {
        amzAdvKeywordService.deleteAmzAdvKeyword(id);
        return success(true);
    }

    @GetMapping("/keyword/get/{id}")
    @Operation(summary = "获取关键词")
    public CommonResult<AmzAdvKeywordDO> getKeyword(@PathVariable("id") Long id) {
        return success(amzAdvKeywordService.getAmzAdvKeyword(id));
    }

    @GetMapping("/keyword/page")
    @Operation(summary = "分页获取关键词")
    public CommonResult<PageResult<AmzAdvKeywordDO>> getKeywordPage(@Valid AmzAdvKeywordPageReqVO pageReqVO) {
        return success(amzAdvKeywordService.getAmzAdvKeywordPage(pageReqVO));
    }

    // ========== 出价策略管理 ==========
    
    @PostMapping("/bid-strategy/create")
    @Operation(summary = "创建出价策略")
    public CommonResult<Long> createBidStrategy(@RequestBody @Valid AmzAdvBidStrategySaveReqVO createReqVO) {
        return success(amzAdvBidStrategyService.createAmzAdvBidStrategy(createReqVO));
    }

    @PutMapping("/bid-strategy/update")
    @Operation(summary = "更新出价策略")
    public CommonResult<Boolean> updateBidStrategy(@RequestBody @Valid AmzAdvBidStrategySaveReqVO updateReqVO) {
        amzAdvBidStrategyService.updateAmzAdvBidStrategy(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/bid-strategy/delete/{id}")
    @Operation(summary = "删除出价策略")
    public CommonResult<Boolean> deleteBidStrategy(@PathVariable("id") Long id) {
        amzAdvBidStrategyService.deleteAmzAdvBidStrategy(id);
        return success(true);
    }

    @GetMapping("/bid-strategy/get/{id}")
    @Operation(summary = "获取出价策略")
    public CommonResult<AmzAdvBidStrategyDO> getBidStrategy(@PathVariable("id") Long id) {
        return success(amzAdvBidStrategyService.getAmzAdvBidStrategy(id));
    }

    @GetMapping("/bid-strategy/page")
    @Operation(summary = "分页获取出价策略")
    public CommonResult<PageResult<AmzAdvBidStrategyDO>> getBidStrategyPage(@Valid AmzAdvBidStrategyPageReqVO pageReqVO) {
        return success(amzAdvBidStrategyService.getAmzAdvBidStrategyPage(pageReqVO));
    }
}