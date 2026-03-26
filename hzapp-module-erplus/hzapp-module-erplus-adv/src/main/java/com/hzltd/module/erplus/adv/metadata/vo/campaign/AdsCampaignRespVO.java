package com.hzltd.module.erplus.adv.metadata.vo.campaign;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 广告计划响应 VO")
@Data
public class AdsCampaignRespVO {

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "广告账户编号", example = "1")
    private Long accountId;

    @Schema(description = "平台原始 Campaign ID", example = "C123")
    private String externalId;

    @Schema(description = "计划名称", example = "Autumn Sale")
    private String name;

    @Schema(description = "计划类型", example = "SP")
    private String campaignType;

    @Schema(description = "广告目标", example = "CONVERSIONS")
    private String objective;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

    @Schema(description = "原始平台状态", example = "active")
    private String platformStatus;

    @Schema(description = "预算类型", example = "DAILY")
    private String budgetType;

    @Schema(description = "日预算", example = "100.00")
    private BigDecimal dailyBudget;

    @Schema(description = "总预算", example = "5000.00")
    private BigDecimal totalBudget;

    @Schema(description = "投放开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @Schema(description = "投放结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "分时投放配置")
    private String deliverySchedule;

    @Schema(description = "出价策略", example = "MANUAL_CPC")
    private String biddingStrategy;

    @Schema(description = "同步时间")
    private LocalDateTime syncedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "账号平台", example = "AMAZON")
    private String platform;

    @Schema(description = "扩展属性")
    private Map<String, Object> attributes;

    @Schema(description = "扩展数据", example = "{\"platformConfig\": {}}")
    private Object extData;

}
