package com.hzltd.module.erplus.controller.admin.shop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 平台应用创建/修改 Request VO")
@Data
public class PlatformAppSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "亚马逊应用")
    @NotEmpty(message = "应用名称不能为空")
    private String name;

    @Schema(description = "平台类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    @NotEmpty(message = "平台类型不能为空")
    private String platform;

    @Schema(description = "应用Key", requiredMode = Schema.RequiredMode.REQUIRED, example = "app_key_xxx")
    @NotEmpty(message = "应用Key不能为空")
    private String appKey;

    @Schema(description = "应用密钥", requiredMode = Schema.RequiredMode.REQUIRED, example = "app_secret_xxx")
    @NotEmpty(message = "应用密钥不能为空")
    private String appSecret;

    @Schema(description = "回调地址", example = "https://example.com/callback")
    private String callbackUrl;

}
