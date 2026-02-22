package com.hzltd.module.erplus.adv.metadata.vo.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 广告响应 VO")
@Data
public class AdsAdRespVO {

    @Schema(description = "广告编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "广告账户编号", example = "1")
    private Long accountId;

    @Schema(description = "广告组编号", example = "20")
    private Long adGroupId;

    @Schema(description = "平台原始 Ad ID", example = "A123")
    private String externalId;

    @Schema(description = "广告名称", example = "Summer Banner")
    private String name;

    @Schema(description = "广告类型", example = "IMAGE")
    private String adType;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

    @Schema(description = "原始平台状态", example = "active")
    private String platformStatus;

    @Schema(description = "同步时间")
    private LocalDateTime syncedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
