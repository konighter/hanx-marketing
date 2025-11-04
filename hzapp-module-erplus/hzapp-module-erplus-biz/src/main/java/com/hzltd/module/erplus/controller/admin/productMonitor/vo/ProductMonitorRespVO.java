package com.hzltd.module.erplus.controller.admin.productMonitor.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.hzltd.framework.excel.core.annotations.DictFormat;
import com.hzltd.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 产品监控 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProductMonitorRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20689")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "产品ID", example = "26769")
    @ExcelProperty("产品ID")
    private String productId;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "6036")
    @ExcelProperty("平台")
    private Integer platformId;

    @Schema(description = "产品链接")
    @ExcelProperty("产品链接")
    private String productLink;

    @Schema(description = "商品主图")
    @ExcelProperty("商品主图")
    private String imageLink;

    @Schema(description = "品类ID", example = "31303")
    @ExcelProperty("品类ID")
    private Integer categoryId;

    @Schema(description = "周期(默认Day)")
    @ExcelProperty("周期(默认Day)")
    private String crone;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("common_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}