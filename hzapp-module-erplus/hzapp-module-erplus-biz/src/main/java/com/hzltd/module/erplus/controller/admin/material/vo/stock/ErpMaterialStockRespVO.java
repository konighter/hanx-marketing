package com.hzltd.module.erplus.controller.admin.material.vo.stock;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - ERP 耗材库存 Response VO")
@Data
public class ErpMaterialStockRespVO {

    @Schema(description = "库存编号", example = "1")
    private Long id;

    @Schema(description = "耗材编号", example = "1")
    private Long materialId;

    @Schema(description = "耗材名称", example = "纸箱")
    private String materialName;

    @Schema(description = "耗材编码", example = "M001")
    private String materialCode;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "上海仓")
    private String warehouseName;

    @Schema(description = "当前库存数量", example = "100")
    private BigDecimal quantity;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
