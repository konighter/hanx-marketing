package com.hzltd.module.erplus.ai.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MAS 会话 Response VO")
@Data
public class MasSessionRespVO {

    @Schema(description = "会话唯一标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-123")
    private String id;

    @Schema(description = "任务目标", requiredMode = Schema.RequiredMode.REQUIRED, example = "分析广告")
    private String goal;

    @Schema(description = "当前状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "EXECUTING")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
