package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;

@Data
public class AmzSetPackingInfoRequest {

    private Integer shopId;
    private String planId;

    private AmzPackingOption option;

}
