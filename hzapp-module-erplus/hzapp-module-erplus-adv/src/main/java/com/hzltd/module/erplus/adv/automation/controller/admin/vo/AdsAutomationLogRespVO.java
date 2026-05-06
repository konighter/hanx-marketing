package com.hzltd.module.erplus.adv.automation.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 广告自动化日志 Response VO")
@Data
public class AdsAutomationLogRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long planId;

    @Schema(description = "触发规则名称", example = "高 ACOS 暂停")
    private String ruleName;

    @Schema(description = "触发指标数据")
    private Map<String, Object> triggerData;

    @Schema(description = "执行动作描述")
    private String actionTaken;

    @Schema(description = "执行结果", example = "SUCCESS")
    private String status;

    @Schema(description = "执行时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
