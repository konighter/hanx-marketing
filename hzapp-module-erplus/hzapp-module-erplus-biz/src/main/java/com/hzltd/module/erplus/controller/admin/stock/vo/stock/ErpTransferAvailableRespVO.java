package com.hzltd.module.erplus.controller.admin.stock.vo.stock;

import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import lombok.Data;

import java.util.List;

@Data
public class ErpTransferAvailableRespVO extends ErpWarehouseInventoryDO {

    private Integer platformId;

    private Integer shopId;

    private String platformProductCode;
}
