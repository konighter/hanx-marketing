package com.hzltd.module.erplus.adv.metadata.controller.admin.campaign;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignRespVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告计划")
@RestController
@RequestMapping("/erplus/adv/campaign")
@Validated
public class AdsCampaignController {

    @Resource
    private AdsCampaignService adsCampaignService;

    @PostMapping("/page")
    @Operation(summary = "获得广告计划分页")
    @PreAuthorize("@ss.hasPermission('erplus:adv-campaign:query')")
    public CommonResult<PageResult<AdsCampaignRespVO>> getCampaignPage(@Valid @RequestBody AdsCampaignPageReqVO pageReqVO) {
        PageResult<AdsCampaignDO> pageResult = adsCampaignService.getCampaignPage(pageReqVO);
        PageResult<AdsCampaignRespVO> result = BeanUtils.toBean(pageResult, AdsCampaignRespVO.class);
        // 聚合属性数据
        if (result != null && !CollectionUtils.isEmpty(result.getList())) {
            result.getList().forEach(vo -> vo.setAttributes(adsCampaignService.getCampaignAttributes(vo.getId())));
        }
        return success(result);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新广告计划状态")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true),
            @Parameter(name = "status", description = "统一状态", required = true)
    })
    @PreAuthorize("@ss.hasPermission('erplus:adv-campaign:update')")
    public CommonResult<Boolean> updateCampaignStatus(@RequestParam("id") Long id,
                                                      @RequestParam("status") String status) {
        adsCampaignService.updateCampaignStatus(id, status);
        return success(true);
    }

    @PutMapping("/update-budget")
    @Operation(summary = "更新广告计划预算")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true),
            @Parameter(name = "budget", description = "预算", required = true)
    })
    @PreAuthorize("@ss.hasPermission('erplus:adv-campaign:update')")
    public CommonResult<Boolean> updateCampaignBudget(@RequestParam("id") Long id,
                                                      @RequestParam("budget") java.math.BigDecimal budget) {
        adsCampaignService.updateCampaignBudget(id, budget);
        return success(true);
    }

    @PutMapping("/update")
    @Operation(summary = "更新广告计划")
    @PreAuthorize("@ss.hasPermission('erplus:adv-campaign:update')")
    public CommonResult<Boolean> updateCampaign(@Valid @RequestBody AdsCampaignUpdateReqVO updateReqVO) {
        adsCampaignService.updateCampaign(updateReqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得广告计划")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-campaign:query')")
    public CommonResult<AdsCampaignRespVO> getCampaign(@RequestParam("id") Long id) {
        AdsCampaignDO campaign = adsCampaignService.getCampaign(id);
        AdsCampaignRespVO respVO = BeanUtils.toBean(campaign, AdsCampaignRespVO.class);
        if (respVO != null) {
            respVO.setAttributes(adsCampaignService.getCampaignAttributes(id));
        }
        return success(respVO);
    }

}
