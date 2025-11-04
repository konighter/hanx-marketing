package com.hzltd.module.erplus.sys.controller.admin.currencies.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - [Erplus] 货币定义分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CurrenciesPageReqVO extends PageParam {

    @Schema(description = "ISO 4217 Currency code (e.g., USD, EUR, JPY)")
    private String code;

    @Schema(description = "货币符号 (e.g., $, €, ¥)")
    private String symbol;

    @Schema(description = "货币名称", example = "李四")
    private String name;

    @Schema(description = "相对于基准货币的汇率 (需定期更新)")
    private BigDecimal exchangeRate;

    @Schema(description = "是否启用")
    private Boolean isActive;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}