package com.hzltd.module.erplus.controller.admin.productpotential.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductPotentialRespVO {
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12370")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("标题")
    private String title;

    @Schema(description = "品类", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer category;

    @Schema(description = "品类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryName;

    @Schema(description = "售卖平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "16721")
    private Integer platformId;

    @Schema(description = "售卖平台名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "16721")
    private String platformName;

    @Schema(description = "产品特性")
    private String feature;      // 产品特性

    @Schema(description = "目标人群")
    private String target;       // 目标人群

    @Schema(description = "卖点")
    private String sellPoints;   // 卖点


    @Schema(description = "竞品分析")
    private List<CompetitorVO> competitorList;

    @Schema(description = "成本预估方案")
    private List<FinanceVO> financeList;

    @Schema(description = "一次性投入")
    private BigDecimal oneTimeInvest;

    @Schema(description = "分析意见")
    private String analysisOpinion;

    @Schema(description = "审核产品")
    private String reviewProduct;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "创建人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creator;



}
