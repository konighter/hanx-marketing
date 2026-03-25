package com.hzltd.module.erplus.controller.admin.stock.vo.stock;

import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import lombok.Data;

@Data
public class ErpTransferAvailableRespVO extends ErpWarehouseInventoryDO {

    private Integer platformId;

    private Integer shopId;

    private String platformProductCode;
}
