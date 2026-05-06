package com.hzltd.module.erplus.adv.automation.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.automation.controller.admin.vo.AdsAutomationLogPageReqVO;
import com.hzltd.module.erplus.adv.automation.controller.admin.vo.AdsAutomationLogRespVO;
import com.hzltd.module.erplus.adv.automation.service.AdsAutomationLogService;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationLogDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告自动化日志")
@RestController
@RequestMapping("/erplus/adv/automation/log")
@Validated
public class AdsAutomationLogController {

    @Resource
    private AdsAutomationLogService adsAutomationLogService;

    @GetMapping("/page")
    @Operation(summary = "获得广告自动化日志分页")
    @PreAuthorize("@ss.hasPermission('adv:automation-plan:query')")
    public CommonResult<PageResult<AdsAutomationLogRespVO>> getLogPage(@Valid AdsAutomationLogPageReqVO pageReqVO) {
        PageResult<AdsAutomationLogDO> pageResult = adsAutomationLogService.getLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AdsAutomationLogRespVO.class));
    }

}
