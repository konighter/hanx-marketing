package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.DeliveryWindowOption;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.TransportationOption;

import java.util.List;

@Data
public class AmzTransportationOption {
    private String shipmentId;
    private List<TransportationOption> transportationOptions;
    private List<DeliveryWindowOption> deliveryWindows;
}
