package com.hzltd.module.erplus.adv.metadata.vo.campaign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Schema(description = "管理后台 - 广告计划创建 Request VO")
@Data
public class AdsCampaignCreateReqVO {

    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "广告计划类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "sp")
    @NotEmpty(message = "广告计划类型不能为空")
    private String campaignType;

    @Schema(description = "投放类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "MANUAL")
    @NotEmpty(message = "投放类型不能为空")
    private String targetingType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLED")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "日预算", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "日预算不能为空")
    private BigDecimal dailyBudget;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "竞价策略")
    private String biddingStrategy;

    @Schema(description = "属性数据")
    private Map<String, Object> attributes;

}
