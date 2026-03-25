package com.hzltd.module.amz.controller.admin.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 亚马逊报告任务 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AmzReportTaskRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21091")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "报告ID", example = "1654")
    @ExcelProperty("报告ID")
    private String reportId;

    @Schema(description = "查询条件")
    @ExcelProperty("查询条件")
    private String query;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "查询状态", example = "2")
    @ExcelProperty("查询状态")
    private Integer status;

    @Schema(description = "报告结果")
    @ExcelProperty("报告结果")
    private String reportResult;

    @Schema(description = "上次检索时间")
    @ExcelProperty("上次检索时间")
    private LocalDateTime lastCheckTime;

    @Schema(description = "结果是否已归档")
    @ExcelProperty("结果是否已归档")
    private Integer isArchive;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}