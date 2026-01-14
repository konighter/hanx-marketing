package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Item;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Shipment;

import java.util.List;

@Data
public class AmzShipment extends Shipment{

    private List<Item> shipmentItems;

    private String shippingMode;

    private String shippingSolution;

    private String deliveryWindowOptionId;

    private String transportationOptionId;

    private String shipmentDate;

}
