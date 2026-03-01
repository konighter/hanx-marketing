package com.hzltd.module.erplus.ai.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Schema(description = "管理后台 - MAS 会话创建 Request VO")
@Data
public class MasSessionCreateReqVO {

    @Schema(description = "任务目标", requiredMode = Schema.RequiredMode.REQUIRED, example = "帮我分析最近一周的广告表现并优化关键词")
    @NotEmpty(message = "任务目标不能为空")
    private String goal;

}
