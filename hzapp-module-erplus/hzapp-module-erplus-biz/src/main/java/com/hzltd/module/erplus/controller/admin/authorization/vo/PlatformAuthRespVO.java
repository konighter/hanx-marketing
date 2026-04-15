package com.hzltd.module.erplus.controller.admin.authorization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 平台授权 Response VO")
@Data
public class PlatformAuthRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "平台类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    private String platform;

    @Schema(description = "授权类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON_SP")
    private String authType;

    @Schema(description = "授权范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "sellingpartnerapi::notifications")
    private String authScope;

    @Schema(description = "区域代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "NA")
    private String region;

    @Schema(description = "卖家ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "A3MW987654321")
    private String sellerId;

    @Schema(description = "过期时间")
    private LocalDateTime expiryTime;

    @Schema(description = "是否是默认授权")
    private Boolean isDefault;

    @Schema(description = "状态 (0: 正常, 1: 过期/无效)", example = "0")
    private Integer status;

}
