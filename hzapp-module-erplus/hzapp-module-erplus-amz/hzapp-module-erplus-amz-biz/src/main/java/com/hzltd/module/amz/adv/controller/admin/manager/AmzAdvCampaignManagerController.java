package com.hzltd.module.amz.adv.controller.admin.manager;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.service.AmzAdvCampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告活动竞价管理与特定配置")
@RestController
@RequestMapping("/erplus/amz/adv/campaign")
@Validated
public class AmzAdvCampaignManagerController {

    @Resource
    private AmzAdvCampaignService amzAdvCampaignService;

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

}
