package com.hzltd.module.erplus.controller.admin.productMonitor.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 产品监控新增/修改 Request VO")
@Data
public class ProductMonitorSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20689")
    private Integer id;

    @Schema(description = "产品ID", example = "26769")
    private String productId;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "6036")
    @NotNull(message = "平台不能为空")
    private Integer platformId;

    @Schema(description = "产品链接")
    private String productLink;

    @Schema(description = "商品主图")
    @ExcelProperty("商品主图")
    private String imageLink;

    @Schema(description = "品类ID", example = "31303")
    private Integer categoryId;

    @Schema(description = "周期(默认Day)")
    private String crone;

    @Schema(description = "周期(默认Day)")
    private String croneType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

}