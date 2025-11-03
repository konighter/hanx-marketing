package com.hzltd.module.erplus.controller.admin.productMonitor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductMetricsRespVO {

    @Schema(name = "指标名")
    private String name;

    @Schema(name = "指标字段")
    private String field;

}
