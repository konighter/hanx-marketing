package com.hzltd.module.erplus.controller.admin.authorization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 平台授权提交 Request VO (支持自授权)")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformAuthSubmitReqVO extends PlatformAuthGenerateReqVO {

    @Schema(description = "是否自授权", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean selfAuth;

    @Schema(description = "刷新令牌 (自授权时必填)")
    private String refreshToken;

}
