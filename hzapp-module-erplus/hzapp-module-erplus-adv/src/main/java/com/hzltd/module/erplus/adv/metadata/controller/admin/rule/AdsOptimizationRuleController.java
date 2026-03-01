package com.hzltd.module.erplus.adv.metadata.controller.admin.rule;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.adv.metadata.service.rule.AdsOptimizationRuleService;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleSaveReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告优化规则")
@RestController
@RequestMapping("/erplus/adv")
@Validated
public class AdsOptimizationRuleController {

    @Resource
    private AdsOptimizationRuleService adsOptimizationRuleService;

    @PostMapping("/ads/rules/optimization")
    @Operation(summary = "创建优化规则")
    @PreAuthorize("@ss.hasPermission('erplus:adv-rule:create')")
    public CommonResult<String> createOptimizationRule(@Valid @RequestBody AdsOptimizationRuleSaveReqVO createReqVO) {
        String ruleId = adsOptimizationRuleService.createOptimizationRule(createReqVO);
        return success(ruleId);
    }


    @GetMapping("/ads/rules/optimization/list")
    @Operation(summary = "获取优化规则列表")
    @PreAuthorize("@ss.hasPermission('erplus:adv-rule:query')")
    public CommonResult<List<AdsOptimizationRuleDO>> getOptimizationRuleList() {
        return success(adsOptimizationRuleService.getOptimizationRuleList());
    }

}
