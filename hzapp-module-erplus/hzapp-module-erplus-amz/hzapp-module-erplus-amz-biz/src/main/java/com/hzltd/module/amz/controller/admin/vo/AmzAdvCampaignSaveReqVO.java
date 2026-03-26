package com.hzltd.module.amz.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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

    @Schema(description = "竞价和位置出价策略")
    private DynamicBidding dynamicBidding;

    @Data
    public static class DynamicBidding {
        @Schema(description = "竞价策略: LEGACY_FOR_SALES, AUTO_FOR_SALES, MANUAL, RULE_BASED_BIDDING")
        private String strategy;

        @Schema(description = "展示位置出价调整")
        private List<PlacementBidding> placementBidding;
    }

    @Data
    public static class PlacementBidding {
        @Schema(description = "调整百分比 (如 50 表示 +50%)")
        private Integer percentage;

        @Schema(description = "展示位置: PLACEMENT_PRODUCT_PAGE, PLACEMENT_TOP_OF_SEARCH 等")
        private String placement;
    }
}