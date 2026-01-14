package com.hzltd.module.erplus.convert.shipment;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentItemVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.StockShipmentPlanReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentPlanDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 货件计划 Convert
 *
 * @author 翰展科技
 */
@Mapper
public interface ShipmentConvert {

    ShipmentConvert INSTANCE = Mappers.getMapper(ShipmentConvert.class);

    /**
     * 转换为 DO 并保存
     */
   default void convert(ShipmentPlanDO shipmentPlanDO, StockShipmentPlanReqVO reqVO) {
       shipmentPlanDO.setId(reqVO.getId());
       shipmentPlanDO.setName(reqVO.getName());
       shipmentPlanDO.setCode(reqVO.getCode());
       shipmentPlanDO.setShopId(reqVO.getShopId());
       shipmentPlanDO.setPlatformId(reqVO.getPlatformId());
       shipmentPlanDO.setWarehouseId(reqVO.getWarehouseId());
       shipmentPlanDO.setDestination(reqVO.getDestination());
       shipmentPlanDO.setRemark(reqVO.getRemark());
   }

   ShipmentItemDO  convert(ShipmentItemVO shipmentItemDO);

}
