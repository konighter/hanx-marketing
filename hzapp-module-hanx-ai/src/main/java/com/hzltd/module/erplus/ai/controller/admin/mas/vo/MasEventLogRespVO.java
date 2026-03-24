package com.hzltd.module.erplus.ai.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MAS 执行事件响应 VO")
@Data
public class MasEventLogRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "会话 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-123")
    private String sessionId;

    @Schema(description = "循环 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "loop-A")
    private String loopId;

    @Schema(description = "事件类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "PHASE_CHANGE")
    private String eventType;

    @Schema(description = "事件描述", example = "THINK")
    private String description;

    @Schema(description = "事件负载", example = "{\"key\": \"val\"}")
    private String payload;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
