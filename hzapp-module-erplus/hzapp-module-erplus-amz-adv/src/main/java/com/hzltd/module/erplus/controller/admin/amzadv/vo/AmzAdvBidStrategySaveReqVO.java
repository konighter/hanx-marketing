package com.hzltd.module.erplus.controller.admin.amzadv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 亚马逊广告出价策略创建/更新 Request VO")
@Data
public class AmzAdvBidStrategySaveReqVO {

    @Schema(description = "ID", example = "123")
    private Long id;

    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "店铺ID不能为空")
    private String shopId;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "动态出价策略")
    @NotNull(message = "策略名称不能为空")
    private String name;

    @Schema(description = "策略类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "dynamic")
    @NotNull(message = "策略类型不能为空")
    private String strategyType; // fixed, dynamic, rule_based

    @Schema(description = "策略配置JSON", example = "{\"minBid\": 0.5, \"maxBid\": 2.0, \"adjustmentRules\": []}")
    private String config;

    @Schema(description = "策略描述", example = "动态调整出价以优化广告效果")
    private String description;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "关联的广告活动类型", example = "sponsoredProducts")
    private String campaignType; // sponsoredProducts, sponsoredBrands, sponsoredDisplay

    @Schema(description = "触发条件", example = "conversion_rate > 0.1")
    private String triggerConditions;

    @Schema(description = "调整幅度百分比", example = "10.0")
    private Double adjustmentPercentage;

    @Schema(description = "最低出价限制", example = "0.5")
    private Double minBid;

    @Schema(description = "最高出价限制", example = "5.0")
    private Double maxBid;

    @Schema(description = "执行频率", example = "daily")
    private String executionFrequency; // hourly, daily, weekly
}