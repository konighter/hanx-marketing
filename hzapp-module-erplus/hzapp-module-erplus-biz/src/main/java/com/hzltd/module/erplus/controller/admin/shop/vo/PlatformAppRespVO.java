package com.hzltd.module.erplus.controller.admin.shop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 平台应用 Response VO")
@Data
public class PlatformAppRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "亚马逊应用")
    private String name;

    @Schema(description = "平台类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    private String platform;

    @Schema(description = "回调地址", example = "https://example.com/callback")
    private String callbackUrl;

}
