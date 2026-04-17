package com.hzltd.module.erplus.controller.admin.material.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - ERP 商品耗材 BOM Response VO")
@Data
public class ErpProductMaterialRespVO {

    @Schema(description = "BOM编号", example = "1")
    private Long id;

    @Schema(description = "商品 SKU 编号", example = "1")
    private Long skuId;

    @Schema(description = "耗材编号", example = "1")
    private Long materialId;

    @Schema(description = "耗材名称", example = "纸箱")
    private String materialName;

    @Schema(description = "耗材编码", example = "M001")
    private String materialCode;

    @Schema(description = "耗材单位", example = "个")
    private String materialUnit;

    @Schema(description = "单个成品的标称用量", example = "1")
    private BigDecimal usageQuantity;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
