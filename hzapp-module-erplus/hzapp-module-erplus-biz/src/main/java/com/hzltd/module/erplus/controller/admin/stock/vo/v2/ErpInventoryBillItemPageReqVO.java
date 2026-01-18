package com.hzltd.module.erplus.controller.admin.stock.vo.v2;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 库存账单明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpInventoryBillItemPageReqVO extends PageParam {

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "SKU", example = "SKU001")
    private String sellerSku;

    @Schema(description = "单据大类", example = "10")
    private Integer type;

    @Schema(description = "业务类型", example = "PO")
    private String refType;

    @Schema(description = "关联单据号", example = "PO001")
    private String refCode;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
