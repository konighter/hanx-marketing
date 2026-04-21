package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Schema(description = "管理后台 - 广告多维报表返回行 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportRowVO {

    @Schema(description = "维度列表")
    private List<DimensionValueVO> dimensions;

    @Schema(description = "指标数值列表")
    private List<MetricValueVO> metrics;
}
