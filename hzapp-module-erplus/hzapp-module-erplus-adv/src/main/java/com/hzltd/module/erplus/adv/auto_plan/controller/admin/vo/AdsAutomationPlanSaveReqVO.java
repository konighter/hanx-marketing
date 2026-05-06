package com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Schema(description = "管理后台 - 广告自动化计划创建/更新 Request VO")
@Data
public class AdsAutomationPlanSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "计划名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "我的自动化计划")
    private String name;

    @Schema(description = "模版编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long templateId;

    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long shopId;

    @Schema(description = "广告平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    private String platform;

    @Schema(description = "商品 SKU/ASIN", example = "SKU123")
    private String sku;

    @Schema(description = "执行上下文配置（阈值等）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> context;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "RUNNING")
    private String status;

}
