package com.hzltd.module.amz.controller.admin.vo;

import lombok.Data;

@Data
public class AmzSetPackingInfoRequest {

    private Integer shopId;
    private String planId;

    private AmzPackingOption option;

}
