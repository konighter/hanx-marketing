package com.hzltd.module.erplus.controller.admin.stock.vo.assembly;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 装配单分页 Request VO")
@Data
public class ErpAssemblyOrderPageReqVO extends PageParam {

    @Schema(description = "业务单号", example = "PROD-20260415-001")
    private String no;

    @Schema(description = "目标 SKU 编号", example = "1")
    private Long skuId;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
