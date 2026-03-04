package com.hzltd.module.erplus.adv.auth.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.adv.adapter.amazon.service.AdsAmazonProfileService;
import com.hzltd.module.erplus.adv.adapter.amazon.service.AdsAmazonReportService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告 Profile")
@RestController
@RequestMapping("/erplus/adv/amazon-profile")
@Validated
public class AdsAmazonProfileController {

    @Resource
    private AdsAmazonProfileService adsAmazonProfileService;

    @Resource
    private AdsAmazonReportService adsAmazonReportService;

    @GetMapping("/list")
    @Operation(summary = "获得亚马逊广告 Profile 列表")
    @Parameter(name = "accountId", description = "账号编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-account:query')")
    public CommonResult<List<AdsAmazonProfileDO>> getAmazonProfileList(@RequestParam("accountId") Long accountId) {
        return success(adsAmazonProfileService.getAmazonProfileList(accountId));
    }

    @GetMapping("/init-stream")
    @Operation(summary = "初始化亚马逊广告 Stream 订阅")
    @Parameter(name = "accountId", description = "账号编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-account:update')")
    public CommonResult<Boolean> initStream(@RequestParam("accountId") Long accountId) {
        adsAmazonReportService.createStreamSubscriptionByAccountId(accountId);
        return success(true);
    }

}
