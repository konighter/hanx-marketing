package com.hzltd.module.erplus.controller.admin.stock.vo.warehouse;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ErpWarehouseInventoryPageReqVO extends PageParam {

    @Schema(description = "产品编号", example = "19614")
    private Long productId;

    @Schema(description = "搜索关键词", example = "产品名称, 产品编号")
    private String keyword;

    @Schema(description = "仓库编号", example = "2802")
    private Long warehouseId;

    @Schema(description = "物料类型", example = "1")
    private Integer itemType;

}