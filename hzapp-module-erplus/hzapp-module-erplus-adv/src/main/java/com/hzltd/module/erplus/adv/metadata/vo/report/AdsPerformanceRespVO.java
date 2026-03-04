package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 广告绩效响应 VO")
@Data
public class AdsPerformanceRespVO {

    @Schema(description = "维度列表")
    private List<AdsDimensionVO> dimensions;

    @Schema(description = "指标列表")
    private List<AdsMetricVO> metrics;

    @Schema(description = "子层级数据 (用于下钻)")
    private List<AdsPerformanceRespVO> children;

}
