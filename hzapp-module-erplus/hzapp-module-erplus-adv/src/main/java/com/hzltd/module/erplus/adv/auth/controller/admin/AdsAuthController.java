package com.hzltd.module.erplus.adv.auth.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountPageReqVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountRespVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAuthCallbackReqVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAuthUrlReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告授权")
@RestController
@RequestMapping("/erplus/adv/auth")
@Validated
public class AdsAuthController {

    @Resource
    private AdsAuthService adsAuthService;

    @GetMapping("/authorize-url")
    @Operation(summary = "获得各渠道广告授权链接")
    @PreAuthorize("@ss.hasPermission('erplus:adv-account:query')")
    public CommonResult<String> getAuthorizeUrl(@Valid AdsAuthUrlReqVO reqVO) {
        return success(adsAuthService.getAuthorizeUrl(reqVO));
    }

    @GetMapping("/callback")
    @Operation(summary = "处理广告授权回调")
    // 回调接口通常需要放行安全校验，或者根据 state 进行校验，这里暂不加权限
    public CommonResult<Boolean> handleCallback(@Valid AdsAuthCallbackReqVO reqVO) {
        adsAuthService.handleCallback(reqVO);
        return success(true);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "同步刷新 Token")
    @Parameter(name = "id", description = "账号编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-account:update')")
    public CommonResult<Boolean> refreshToken(@RequestParam("id") Long id) {
        adsAuthService.refreshToken(id);
        return success(true);
    }

    @GetMapping("/account-list")
    @Operation(summary = "获得广告账号列表")
    @PreAuthorize("@ss.hasPermission('erplus:adv-account:query')")
    public CommonResult<List<AdsAccountRespVO>> getAccountList() {
        List<AdsAccountDO> list = adsAuthService.getAccountList();
        return success(BeanUtils.toBean(list, AdsAccountRespVO.class));
    }

    @GetMapping("/account/page")
    @Operation(summary = "获得广告账号分页")
    @PreAuthorize("@ss.hasPermission('erplus:adv-account:query')")
    public CommonResult<PageResult<AdsAccountRespVO>> getAccountPage(@Valid AdsAccountPageReqVO reqVO) {
        PageResult<AdsAccountDO> pageResult = adsAuthService.getAccountPage(reqVO);
        return success(BeanUtils.toBean(pageResult, AdsAccountRespVO.class));
    }

    @DeleteMapping("/account-delete")
    @Operation(summary = "删除广告账号")
    @Parameter(name = "id", description = "账号编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-account:delete')")
    public CommonResult<Boolean> deleteAccount(@RequestParam("id") Long id) {
        adsAuthService.deleteAccount(id);
        return success(true);
    }

}
