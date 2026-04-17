package com.hzltd.module.erplus.controller.admin.stock.vo.assembly;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 装配单耗材明细 Response VO")
@Data
public class ErpAssemblyItemRespVO {

    @Schema(description = "明细编号", example = "1")
    private Long id;

    @Schema(description = "关联装配单编号", example = "1")
    private Long orderId;

    @Schema(description = "耗材编号", example = "1")
    private Long materialId;

    @Schema(description = "耗材名称", example = "纸箱")
    private String materialName;

    @Schema(description = "耗材编码", example = "M001")
    private String materialCode;

    @Schema(description = "耗材单位", example = "个")
    private String materialUnit;

    @Schema(description = "应耗数量", example = "10")
    private BigDecimal expectedQty;

    @Schema(description = "缺料数量", example = "0")
    private BigDecimal shortfallQty;

    @Schema(description = "是否缺料", example = "false")
    private Boolean isShortfall;

}
