package com.hzltd.module.erplus.controller.admin.productpotential.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 选品提案新增/修改 Request VO")
@Data
public class ProductPotentialSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12370")
    private Integer id;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类不能为空")
    private Integer category;

    @Schema(description = "售卖平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "16721")
    @NotNull(message = "售卖平台不能为空")
    private Integer platformId;

    @Schema(description = "产品特性")
    @NotEmpty(message = "产品特性不能为空")
    private String feature;      // 产品特性

    @Schema(description = "目标人群")
    @NotEmpty(message = "目标人群不能为空")
    private String target;       // 目标人群

    @Schema(description = "卖点")
    @NotEmpty(message = "卖点不能为空")
    private String sellPoints;   // 卖点


    @Schema(description = "竞品分析")
    @NotNull(message = "竞品分析不能为空")
    private List<CompetitorVO> competitorList;

    @Schema(description = "成本预估方案")
    @NotNull(message = "成本预估方案不能为空")
    private List<FinanceVO> financeList;

    @Schema(description = "一次性投入")
    private BigDecimal oneTimeInvest;
    @Schema(description = "分析意见")
    private String analysisOpinion;

    @Schema(description = "审核产品")
    private String reviewProduct;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

}