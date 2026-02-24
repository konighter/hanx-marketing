package com.hzltd.module.erplus.adv.metadata.vo.campaign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 广告计划更新 VO")
@Data
public class AdsCampaignUpdateReqVO {

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "计划编号不能为空")
    private Long id;

    @Schema(description = "计划名称", example = "Autumn Sale")
    private String name;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

    @Schema(description = "日预算", example = "100.00")
    private BigDecimal dailyBudget;

    @Schema(description = "总预算", example = "5000.00")
    private BigDecimal totalBudget;

    @Schema(description = "投放开始日期")
    private LocalDate startDate;

    @Schema(description = "投放结束日期")
    private LocalDate endDate;

    @Schema(description = "分时投放配置")
    private String deliverySchedule;

    @Schema(description = "平台扩展字段 (JSON)")
    private Object extData;

}
