package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

import java.util.List;

@Data
public class AmzSetPackingInfoRequest {

    private Integer shopId;
    private String planId;
    private AmzPackingOption option;

}
