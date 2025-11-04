package com.hzltd.module.erplus.sys.controller.admin.currencies.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - [Erplus] 货币定义 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CurrenciesRespVO {

    @Schema(description = "货币ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12218")
    @ExcelProperty("货币ID")
    private Integer id;

    @Schema(description = "ISO 4217 Currency code (e.g., USD, EUR, JPY)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("ISO 4217 Currency code (e.g., USD, EUR, JPY)")
    private String code;

    @Schema(description = "货币符号 (e.g., $, €, ¥)")
    @ExcelProperty("货币符号 (e.g., $, €, ¥)")
    private String symbol;

    @Schema(description = "货币名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("货币名称")
    private String name;

    @Schema(description = "相对于基准货币的汇率 (需定期更新)")
    @ExcelProperty("相对于基准货币的汇率 (需定期更新)")
    private BigDecimal exchangeRate;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否启用")
    private Boolean isActive;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}