package com.hzltd.module.erplus.adv.auto_plan.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationPlanPageReqVO;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationPlanResp;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationPlanSaveReqVO;
import com.hzltd.module.erplus.adv.auto_plan.service.AdsAutomationPlanService;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - 广告自动化计划")
@RestController
@RequestMapping("/erplus/adv/automation/plan")
@Validated
public class AdsAutomationPlanController {

    @Resource
    private AdsAutomationPlanService adsAutomationPlanService;


    @PostMapping("/create")
    @Operation(summary = "创建广告自动化计划")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:create')")
    public CommonResult<Long> createPlan(@Valid @RequestBody AdsAutomationPlanSaveReqVO createReqVO) {
        return success(adsAutomationPlanService.createPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新广告自动化计划")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:update')")
    public CommonResult<Boolean> updatePlan(@Valid @RequestBody AdsAutomationPlanSaveReqVO updateReqVO) {
        adsAutomationPlanService.updatePlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除广告自动化计划")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:delete')")
    public CommonResult<Boolean> deletePlan(@RequestParam("id") Long id) {
        adsAutomationPlanService.deletePlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得广告自动化计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:query')")
    public CommonResult<AdsAutomationPlanResp> getPlan(@RequestParam("id") Long id) {
        AdsAutomationPlanResp plan = adsAutomationPlanService.getPlan(id);
        return success(plan);
    }

    @GetMapping("/page")
    @Operation(summary = "获得广告自动化计划分页")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:query')")
    public CommonResult<PageResult<AdsAutomationPlanResp>> getPlanPage(@Valid AdsAutomationPlanPageReqVO pageReqVO) {
        PageResult<AdsAutomationPlanResp> pageResult = adsAutomationPlanService.getPlanPage(pageReqVO);

        return success(pageResult);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新计划状态（启动/暂停）")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:update')")
    public CommonResult<Boolean> updatePlanStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        AdsAutomationPlanSaveReqVO updateVO = new AdsAutomationPlanSaveReqVO();
        updateVO.setId(id);
        updateVO.setStatus(status);
        adsAutomationPlanService.updatePlan(updateVO);
        return success(true);
    }

}
