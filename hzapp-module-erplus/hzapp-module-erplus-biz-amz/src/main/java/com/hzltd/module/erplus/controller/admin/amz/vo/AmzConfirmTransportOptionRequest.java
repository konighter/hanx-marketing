package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

import java.util.List;

@Data
public class AmzConfirmTransportOptionRequest {
    private Integer shopId;
    private String planId;
    List<AmzShipment> shipments;
}
