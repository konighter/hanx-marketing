package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;

@Data
public class AmzConfirmDeliveryWindowRequest {

        private Integer shopId;

    private String planId;

    private String shipmentId;

    private String deliveryWindowOptionId;



}
