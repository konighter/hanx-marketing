package com.hzltd.module.erplus.controller.admin.stock.vo.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentItemVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentPlanDO;
import lombok.Data;

import java.util.List;

@Data
public class StockShipmentPageRespVO extends ShipmentPlanDO {


    private List<ShipmentItemVO> items;

}
