package com.hzltd.module.erplus.controller.admin.stock.vo.assembly;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 装配单保存 Request VO")
@Data
public class ErpAssemblyOrderSaveReqVO {

    @Schema(description = "装配单编号", example = "1")
    private Long id;

    @Schema(description = "业务单号", example = "PROD-20260415-001")
    private String no;

    @Schema(description = "目标 SKU 编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long skuId;

    @Schema(description = "仓库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long warehouseId;

    @Schema(description = "计划生产数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private BigDecimal plannedQty;

    @Schema(description = "实际生产数量", example = "100")
    private BigDecimal actualQty;

    @Schema(description = "生产批次号", example = "PROD-20260415-001")
    private String batchNo;

    @Schema(description = "备注", example = "用于生产测试")
    private String remark;

}
