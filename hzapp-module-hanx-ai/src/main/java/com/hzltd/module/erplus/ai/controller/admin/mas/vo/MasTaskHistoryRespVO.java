package com.hzltd.module.erplus.ai.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MAS 任务执行日志 Response VO")
@Data
public class MasTaskHistoryRespVO {

    @Schema(description = "日志编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "关键词提取")
    private String name;

    @Schema(description = "执行角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "pm")
    private String role;

    @Schema(description = "提示词内容", example = "提取关键词...")
    private String prompt;

    @Schema(description = "执行结果", example = "结果列表...")
    private String result;

    @Schema(description = "执行状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUCCESS")
    private String status;

    @Schema(description = "执行耗时(ms)", example = "1500")
    private Long executionTime;

    @Schema(description = "是否内部任务", example = "false")
    private Boolean isInternal;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
