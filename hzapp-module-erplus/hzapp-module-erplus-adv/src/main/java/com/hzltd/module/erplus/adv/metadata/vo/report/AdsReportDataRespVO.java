package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Schema(description = "管理后台 - 广告多维报表响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportDataRespVO {

    @Schema(description = "多维数据行列表")
    private List<AdsReportRowVO> rows;

    @Schema(description = "全局汇总行（仅含总体指标计算，无维度）")
    private AdsReportRowVO summary;

    @Schema(description = "总行数（用于分页）")
    private Long total;
}
