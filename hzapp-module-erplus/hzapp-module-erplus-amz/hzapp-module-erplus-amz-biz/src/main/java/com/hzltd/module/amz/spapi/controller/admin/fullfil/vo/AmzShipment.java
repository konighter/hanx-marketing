package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Item;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Shipment;

import java.util.List;

@Data
public class AmzShipment extends Shipment{

    private List<Item> shipmentItems;

    private List<AmzBox> boxes = Lists.newArrayList();

    private String shippingMode;

    private String shippingSolution;

    private String deliveryWindowOptionId;

    private String transportationOptionId;

    private String shipmentDate;

    private boolean confirmed = false;


    public AmzShipment addBox(AmzBox box) {
        boxes.add(box);
        return this;
    }

}
