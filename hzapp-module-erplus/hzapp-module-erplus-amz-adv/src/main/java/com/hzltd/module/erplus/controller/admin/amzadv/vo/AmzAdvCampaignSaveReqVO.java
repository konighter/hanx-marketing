package com.hzltd.module.erplus.controller.admin.amzadv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 亚马逊广告活动创建/更新 Request VO")
@Data
public class AmzAdvCampaignSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long id;

    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "店铺ID不能为空")
    private String shopId;

    @Schema(description = "广告活动名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "夏季促销活动")
    @NotNull(message = "广告活动名称不能为空")
    private String name;

    @Schema(description = "广告活动状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "enabled")
    @NotNull(message = "广告活动状态不能为空")
    private String state; // enabled, paused, archived

    @Schema(description = "广告类型", example = "sponsoredProducts")
    private String campaignType; // sponsoredProducts, sponsoredBrands, sponsoredDisplay

    @Schema(description = "每日预算", example = "10.0")
    private Double dailyBudget;

    @Schema(description = "出价策略", example = "manual")
    private String biddingStrategy; // legacyForSales, autoForSales, manual

    @Schema(description = "目标市场", example = "auto")
    private String targetingType; // auto, manual

    @Schema(description = "描述", example = "这是夏季促销活动的描述")
    private String description;
}