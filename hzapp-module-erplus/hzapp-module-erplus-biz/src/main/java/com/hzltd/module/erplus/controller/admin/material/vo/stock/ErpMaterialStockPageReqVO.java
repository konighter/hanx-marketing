package com.hzltd.module.erplus.controller.admin.material.vo.stock;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - ERP 耗材库存分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpMaterialStockPageReqVO extends PageParam {

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "耗材编号", example = "1")
    private Long materialId;

}
