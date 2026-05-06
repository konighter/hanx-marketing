package com.hzltd.module.erplus.adv.automation.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 广告自动化计划 Response VO")
@Data
public class AdsAutomationPlanResp {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "计划名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "我的自动化计划")
    private String name;

    @Schema(description = "模版编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long templateId;

    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long shopId;

    @Schema(description = "店铺名称", example = "亚马逊美国站")
    private String shopName;

    @Schema(description = "广告平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    private String platform;

    @Schema(description = "商品 SKU/ASIN", example = "SKU123")
    private String sku;

    @Schema(description = "商品标题")
    private String productTitle;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "执行上下文配置", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> context;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "RUNNING")
    private String status;

    @Schema(description = "最后运行时间")
    private LocalDateTime lastRunAt;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
