package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;

@Data
public class AmzConfirmPackingOptionRequest {

    private Integer shopId;
    private String planId;
    private String packingOptionId;
}
