package com.hzltd.module.amz.controller.admin.vo;

import lombok.Data;

@Data
public class AmzConfirmPackingOptionRequest {

    private Integer shopId;
    private String planId;
    private String packingOptionId;
}
