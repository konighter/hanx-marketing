package com.hzltd.module.erplus.adv.metadata.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 广告数据分析返回 VO")
@Data
public class AdDataAnalysisRespVO {

    @Schema(description = "日期标识", example = "2023-10-01")
    private LocalDate date;

    @Schema(description = "展现量", example = "154200")
    private Long impressions;

    @Schema(description = "点击量", example = "8560")
    private Long clicks;

    @Schema(description = "点击率 (CTR)", example = "0.055")
    private BigDecimal ctr;

    @Schema(description = "花费 (Spend)", example = "5280.00")
    private BigDecimal spend;

    @Schema(description = "转化成本 (CPA/CPC)", example = "0.61")
    private BigDecimal cpc;

    @Schema(description = "转化数 (Orders)", example = "425")
    private Long orders;

    @Schema(description = "销售额 (Sales)", example = "25600.00")
    private BigDecimal sales;

    @Schema(description = "投资回报率 (ROAS)", example = "4.85")
    private BigDecimal roas;
}
