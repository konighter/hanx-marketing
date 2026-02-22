package com.hzltd.module.erplus.adv.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 广告账户响应 VO")
@Data
public class AdsAccountRespVO {

    @Schema(description = "账户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "广告平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    private String platform;

    @Schema(description = "所属店铺", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long shopId;

    @Schema(description = "平台原始账户ID", example = "A123")
    private String externalAccountId;

    @Schema(description = "账户名称", example = "My Amazon Store")
    private String name;

    @Schema(description = "币种", example = "USD")
    private String currency;

    @Schema(description = "授权状态 (1: 有效, 0: 失效)", example = "1")
    private Integer authStatus;

    @Schema(description = "上次同步时间")
    private LocalDateTime lastSyncedAt;

}
