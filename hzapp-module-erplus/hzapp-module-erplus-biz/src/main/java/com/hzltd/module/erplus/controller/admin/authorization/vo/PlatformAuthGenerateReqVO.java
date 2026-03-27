package com.hzltd.module.erplus.controller.admin.authorization.vo;

import com.hzltd.module.system.enums.CrossPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 平台授权生成地址 Request VO")
@Data
public class PlatformAuthGenerateReqVO {

    @Schema(description = "平台类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    @NotNull(message = "平台类型不能为空")
    private CrossPlatformEnum platform;

    @Schema(description = "授权范围 (AMAZON_SP, AMAZON_ADV, TTS_SHOP)", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON_SP")
    @NotBlank(message = "授权范围不能为空")
    private String authScope;

    @Schema(description = "区域代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "NA")
    @NotBlank(message = "区域代码不能为空")
    private String region;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "卖家ID (可选，部分平台由系统配置)")
    private String sellerId;

    @Schema(description = "刷新令牌 (可选，部分平台由系统配置)")    
    private String refreshToken;

}
