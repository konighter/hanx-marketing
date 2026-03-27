package com.hzltd.module.erplus.controller.admin.authorization;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthGenerateReqVO;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthSubmitReqVO;
import com.hzltd.module.erplus.service.authorization.PlatformAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台授权")
@RestController
@RequestMapping("/erplus/platform-auth")
@Validated
public class PlatformAuthController {

    @Resource
    private PlatformAuthService platformAuthService;

    @Value("${hzapp.web.admin-ui.url}")
    private String adminUiUrl;

    @PostMapping("/generate-url")
    @Operation(summary = "生成授权跳转地址")
    public CommonResult<String> generateAuthUrl(@Valid @RequestBody PlatformAuthGenerateReqVO reqVO) {
        return success(platformAuthService.generateAuthUrl(reqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交授权 (支持自授权)")
    public CommonResult<Boolean> submitAuth(@Valid @RequestBody PlatformAuthSubmitReqVO reqVO) {
        platformAuthService.submitAuth(reqVO);
        return success(true);
    }

    @GetMapping("/callback/{authScope}")
    @Operation(summary = "授权回调处理")
    public void oauthCallback(@PathVariable("authScope") String authScope,
                                @RequestParam("code") String code,
                                @RequestParam("state") String state,
                                HttpServletResponse response) throws IOException {
        try {
            platformAuthService.handleCallback(authScope, code, state);
            response.sendRedirect(adminUiUrl + "/#/auth/callback?status=success");
        } catch (Exception e) {
            response.sendRedirect(adminUiUrl + "/#/auth/callback?status=error&message=" + e.getMessage());
        }
    }
    @PostMapping("/refresh/{shopId}")
    @Operation(summary = "刷新授权 (通过 shopId)")
    public CommonResult<Boolean> refreshAuth(@PathVariable("shopId") Long shopId) {
        platformAuthService.refreshAuth(shopId);
        return success(true);
    }
}
