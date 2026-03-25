package com.hzltd.module.erplus.ai.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - MAS 会话反馈 Request VO")
@Data
public class MasFeedbackReqVO {

    @Schema(description = "会话 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-123")
    @NotEmpty(message = "会话 ID 不能为空")
    private String sessionId;

    @Schema(description = "反馈内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "我觉得这个优化方案还可以改进...")
    @NotEmpty(message = "反馈内容不能为空")
    private String content;

}
