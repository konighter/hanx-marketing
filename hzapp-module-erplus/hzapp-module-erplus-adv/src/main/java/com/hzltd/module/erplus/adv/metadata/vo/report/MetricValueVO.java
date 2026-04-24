package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Schema(description = "管理后台 - 广告报表指标值 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricValueVO {

    @Schema(description = "指标标识", example = "spend")
    private String key;

    @Schema(description = "原始数字数值", example = "120.5")
    private Object value;
}
