package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "管理后台 - 广告绩效响应 VO")
@Data
public class AdsPerformanceRespVO {

    @Schema(description = "核心指标数据 (spend, sales, impressions, clicks, orders, etc.)")
    private Map<String, Object> metrics;

    @Schema(description = "环比趋势数据 (百分比)")
    private Map<String, BigDecimal> trends;

    @Schema(description = "总花费", example = "128.50")
    private BigDecimal spend;

    @Schema(description = "展现量", example = "10000")
    private Long impressions;

    @Schema(description = "点击量", example = "500")
    private Long clicks;

    @Schema(description = "销售额", example = "1500.00")
    private BigDecimal sales;

    @Schema(description = "转化数", example = "50")
    private Integer orders;

    @Schema(description = "ROAS", example = "11.67")
    private BigDecimal roas;

    @Schema(description = "CPC", example = "0.25")
    private BigDecimal cpc;
}
