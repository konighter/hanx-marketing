package com.hzltd.module.erplus.adv.auto_plan.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationTemplatePageReqVO;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationTemplateRespVO;
import com.hzltd.module.erplus.adv.auto_plan.service.AdsAutomationTemplateService;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationTemplateDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告自动化模版")
@RestController
@RequestMapping("/erplus/adv/automation/template")
@Validated
public class AdsAutomationTemplateController {

    @Resource
    private AdsAutomationTemplateService adsAutomationTemplateService;

    @GetMapping("/page")
    @Operation(summary = "获得广告自动化模版分页")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:query')")
    public CommonResult<PageResult<AdsAutomationTemplateRespVO>> getTemplatePage(@Valid AdsAutomationTemplatePageReqVO pageReqVO) {
        PageResult<AdsAutomationTemplateDO> pageResult = adsAutomationTemplateService.getTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AdsAutomationTemplateRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得广告自动化模版")
    @Parameter(name = "id", description = "编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:query')")
    public CommonResult<AdsAutomationTemplateRespVO> getTemplate(@RequestParam("id") Long id) {
        AdsAutomationTemplateDO template = adsAutomationTemplateService.getTemplate(id);
        return success(BeanUtils.toBean(template, AdsAutomationTemplateRespVO.class));
    }

}
