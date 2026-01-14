package com.hzltd.module.erplus.controller.admin.stock.vo.stock;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ErpTransferAvailableReqVO {
    /**
     * 出库仓库
     */
    @NotEmpty
    private Long warehouseId;

    /**
     * 入库仓库
     */
    @NotEmpty
    private Long inboundWarehouseId;

     /**
      * 产品编号
      */
    private List<String> skus;

}
