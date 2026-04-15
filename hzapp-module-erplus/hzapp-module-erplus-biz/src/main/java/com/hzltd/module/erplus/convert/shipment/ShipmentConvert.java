package com.hzltd.module.erplus.convert.shipment;

import com.hzltd.module.amz.controller.admin.vo.AmzInboundPlanCreateRequest;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentItemVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.StockShipmentPlanReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.StockShipmentPlanRespVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAddressDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentPlanDO;
import com.hzltd.module.erplus.spapi.model.common.AddressModel;
import com.hzltd.module.erplus.spapi.model.inventory.InventoryItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

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


   default AmzInboundPlanCreateRequest convert(StockShipmentPlanRespVO resp) {

       AmzInboundPlanCreateRequest amzInboundPlanCreateRequest = new AmzInboundPlanCreateRequest()
               .setName(resp.getName())
               .setShopId(resp.getShopId())
               .setPlatformId(resp.getPlatformId())
               .setMarketId(resp.getDestination());

       amzInboundPlanCreateRequest.setAddress(convert(resp.getAddressDetail()));
       amzInboundPlanCreateRequest.setItems(resp.getItems().stream().map(this::convertToInventoryItemModel).collect(Collectors.toList()));

       return amzInboundPlanCreateRequest;
   }

   default AddressModel convert(ErpAddressDO erpAddressDO) {
       return new AddressModel()
               .setName(erpAddressDO.getContactName())
               .setPhoneNumber(erpAddressDO.getPhoneNumber())
               .setAddressLine1(erpAddressDO.getAddressLine1())
               .setAddressLine2(erpAddressDO.getAddressLine2())
               .setCity(erpAddressDO.getCity())
               .setCountryCode(erpAddressDO.getCountryCode())
               .setPostalCode(erpAddressDO.getPostalCode())
               .setStateOrProvinceCode(erpAddressDO.getDistrictOrCountry());
   }

   default InventoryItemModel convertToInventoryItemModel(ShipmentItemDO shipmentItemDO) {
       return new InventoryItemModel()
               .setSellerSku(shipmentItemDO.getSellerSku())
               .setQuantity(shipmentItemDO.getQuantity());
   }


}
