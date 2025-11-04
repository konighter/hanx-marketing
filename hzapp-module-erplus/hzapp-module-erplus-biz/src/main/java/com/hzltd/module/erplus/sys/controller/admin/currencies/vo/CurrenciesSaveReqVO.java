package com.hzltd.module.erplus.sys.controller.admin.currencies.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - [Erplus] 货币定义新增/修改 Request VO")
@Data
public class CurrenciesSaveReqVO {

    @Schema(description = "货币ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12218")
    private Integer id;

    @Schema(description = "ISO 4217 Currency code (e.g., USD, EUR, JPY)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ISO 4217 Currency code (e.g., USD, EUR, JPY)不能为空")
    private String code;

    @Schema(description = "货币符号 (e.g., $, €, ¥)")
    private String symbol;

    @Schema(description = "货币名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "货币名称不能为空")
    private String name;

    @Schema(description = "相对于基准货币的汇率 (需定期更新)")
    private BigDecimal exchangeRate;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否启用不能为空")
    private Boolean isActive;

}