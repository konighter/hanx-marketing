package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;

import java.util.List;

@Data
public class AmzConfirmTransportOptionRequest {
    private Integer shopId;
    private String planId;
    List<AmzShipment> shipments;
}
