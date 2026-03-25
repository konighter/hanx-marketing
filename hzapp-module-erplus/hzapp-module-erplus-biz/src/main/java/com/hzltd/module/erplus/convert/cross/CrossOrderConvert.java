package com.hzltd.module.erplus.convert.cross;

import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderItemDO;
import com.hzltd.module.spapi.model.order.OrderItemModel;
import com.hzltd.module.spapi.model.order.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrossOrderConvert {
    CrossOrderConvert INSTANCE = Mappers.getMapper(CrossOrderConvert.class);


    default CrossOrderDO convert(OrderModel orderModel) {
        CrossOrderDO crossOrderDO = new CrossOrderDO();
        return update(orderModel, crossOrderDO);
    }


    default CrossOrderDO update(OrderModel orderModel, CrossOrderDO crossOrderDO) {
        crossOrderDO.setMarketId(orderModel.getMarketId());
        crossOrderDO.setFulfillType(orderModel.getFulfillmentType().getCode());
        crossOrderDO.setPlatformOrderId(orderModel.getOrderId());
        crossOrderDO.setCurrency(orderModel.getCurrency());
        crossOrderDO.setOrderType(orderModel.getOrderType());
        crossOrderDO.setAmount(orderModel.getTotalAmount());
        crossOrderDO.setStatus(orderModel.getOrderStatus().getStatus());
        crossOrderDO.setSaleChannel(orderModel.getSalesChannel());
        crossOrderDO.setIsPrime(orderModel.getIsPrime());
        crossOrderDO.setIsBusiness(orderModel.getIsBusiness());
        crossOrderDO.setCreateTime(orderModel.getCreateTime());
        crossOrderDO.setUpdateTime(orderModel.getUpdateTime());
        return crossOrderDO;
    }

    default CrossOrderItemDO convert(OrderItemModel orderItemModel) {
        CrossOrderItemDO crossOrderItemDO = new CrossOrderItemDO();
        return update(orderItemModel, crossOrderItemDO);
    }

    default CrossOrderItemDO update(OrderItemModel orderItemModel, CrossOrderItemDO crossOrderItemDO) {
        crossOrderItemDO.setPlatformOrderId(orderItemModel.getOrderId())
                .setPlatformProductCode(orderItemModel.getPlatformProductCode())
                .setSellerSkuCode(orderItemModel.getSellerSku())
                .setItemCount(orderItemModel.getQuantity())
                .setTitle(orderItemModel.getTitle())
                .setCurrency(orderItemModel.getCurrency())
                .setTotalAmount(orderItemModel.getTotalPrice())
                .setItemPrice(orderItemModel.getItemPrice())
                .setItemTax(orderItemModel.getItemTax())
                .setShipFee(orderItemModel.getShipFee())
                .setShipFeeTax(orderItemModel.getShipFeeTax())
                .setShipFeeDiscount(orderItemModel.getShipFeeDiscount())
                .setShipFeeDiscountTax(orderItemModel.getShipFeeDiscountTax())
                .setPromoDiscount(orderItemModel.getPromoDiscount())
                .setPromoDiscountTax(orderItemModel.getPromoDiscountTax())
                .setCodFee(orderItemModel.getCodFee())
                .setCodFeeDiscount(orderItemModel.getCodFeeDiscount())
                .setPointsNum(orderItemModel.getPointsNum())
                .setPointsAsMoney(orderItemModel.getPointsAsMoney());

        return crossOrderItemDO;
    }


}
