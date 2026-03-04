package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 广告指标 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsMetricVO {

    @Schema(description = "指标标识", example = "spend")
    private String key;

    @Schema(description = "指标名称", example = "花费")
    private String name;

    @Schema(description = "当前数值", example = "128.5")
    private Object value;

    @Schema(description = "环比增长率 (百分比)", example = "10.5")
    private BigDecimal trend;

    @Schema(description = "单位", example = "$")
    private String unit;

}
