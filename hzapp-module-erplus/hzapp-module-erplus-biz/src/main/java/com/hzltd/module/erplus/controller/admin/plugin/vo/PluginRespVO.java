package com.hzltd.module.erplus.controller.admin.plugin.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 插件 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PluginRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23616")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "名称", example = "芋艿")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "插件标识")
    @ExcelProperty("插件标识")
    private String pluginKey;

    @Schema(description = "状态", example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}