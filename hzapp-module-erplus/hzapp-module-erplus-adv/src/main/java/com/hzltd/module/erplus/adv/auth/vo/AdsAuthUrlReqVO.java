package com.hzltd.module.erplus.adv.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 广告授权链接请求 VO")
@Data
public class AdsAuthUrlReqVO {

    @Schema(description = "广告平台", example = "AMAZON")
    private String platform;

    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "回传状态值 (建议由后端生成)", example = "state_123")
    private String state;

}
