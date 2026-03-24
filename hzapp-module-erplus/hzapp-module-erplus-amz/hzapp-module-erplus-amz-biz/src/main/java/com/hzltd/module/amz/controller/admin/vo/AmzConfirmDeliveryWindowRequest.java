package com.hzltd.module.amz.controller.admin.vo;

import lombok.Data;

@Data
public class AmzConfirmDeliveryWindowRequest {

        private Integer shopId;

    private String planId;

    private String shipmentId;

    private String deliveryWindowOptionId;



}
