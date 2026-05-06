package com.hzltd.module.erplus.adv.automation.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 广告自动化模版 Response VO")
@Data
public class AdsAutomationTemplateRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long id;

    @Schema(description = "模版名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "高 ACOS 流转模版")
    private String name;

    @Schema(description = "模版类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "KEYWORD_FLOW")
    private String type;

    @Schema(description = "配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private Object config;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
