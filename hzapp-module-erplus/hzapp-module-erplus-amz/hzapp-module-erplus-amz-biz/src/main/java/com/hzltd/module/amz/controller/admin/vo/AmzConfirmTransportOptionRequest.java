package com.hzltd.module.amz.controller.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class AmzConfirmTransportOptionRequest {
    private Integer shopId;
    private String planId;
    List<AmzShipment> shipments;
}
