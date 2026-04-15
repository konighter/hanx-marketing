package com.hzltd.module.amz.spapi.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "管理后台 - 亚马逊联动逻辑表达式 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogicExpressionVO {

    @Schema(description = "操作符: AND, OR, NOT, EQ, CONTAINS, REQUIRED", example = "EQ")
    private String operator;

    @Schema(description = "字段 ID", example = "parentage_level")
    private String field;

    @Schema(description = "比较值 (用于 EQ, CONTAINS)", example = "parent")
    private Object value;

    @Schema(description = "子表达式", example = "[]")
    private List<LogicExpressionVO> children;

}
