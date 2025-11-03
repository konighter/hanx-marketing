package com.hzltd.module.erplus.controller.admin.productpotential.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 选品提案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProductPotentialPageRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12370")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("标题")
    private String title;

    @Schema(description = "售卖平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "16721")
    @ExcelProperty("售卖平台")
    private Integer platformId;

    @Schema(description = "售卖平台名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "16721")
    @ExcelProperty("售卖平台名称")
    private String platformName;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "状态名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "已创建")
    @ExcelProperty("状态名称")
    private String statusName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建人ID")
    private String creator;

}