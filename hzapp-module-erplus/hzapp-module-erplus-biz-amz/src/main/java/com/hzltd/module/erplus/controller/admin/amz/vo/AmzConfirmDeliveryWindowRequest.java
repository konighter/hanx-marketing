package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

@Data
public class AmzConfirmDeliveryWindowRequest {

        private Integer shopId;

    private String planId;

    private String shipmentId;

    private String deliveryWindowOptionId;



}
