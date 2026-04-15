package com.hzltd.module.erplus.adv.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 广告授权回调请求 VO")
@Data
public class AdsAuthCallbackReqVO {

    @Schema(description = "广告平台", example = "AMAZON")
    private String platform;

    @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "code_123")
    @NotEmpty(message = "授权码不能为空")
    private String code;

    @Schema(description = "状态值", requiredMode = Schema.RequiredMode.REQUIRED, example = "state_123")
    @NotEmpty(message = "状态值不能为空")
    private String state;

}
