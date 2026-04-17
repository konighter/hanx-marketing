package com.hzltd.module.erplus.controller.admin.material.vo.material;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 耗材保存 Request VO")
@Data
public class ErpMaterialSaveReqVO {

    @Schema(description = "耗材编号", example = "1")
    private Long id;

    @Schema(description = "耗材名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "纸箱")
    private String name;

    @Schema(description = "耗材编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "M001")
    private String code;

    @Schema(description = "耗材类型", example = "packaging")
    private String category;

    @Schema(description = "单位", example = "1")
    private Long unit;

    @Schema(description = "长(cm)", example = "10.00")
    private BigDecimal length;

    @Schema(description = "宽(cm)", example = "10.00")
    private BigDecimal width;

    @Schema(description = "高(cm)", example = "10.00")
    private BigDecimal height;

    @Schema(description = "重量(g)", example = "500")
    private BigDecimal weight;

    @Schema(description = "体积(cm³)", example = "1000")
    private BigDecimal volume;

    @Schema(description = "容积")
    private BigDecimal capacity;

    @Schema(description = "备注", example = "用于包装")
    private String remark;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
