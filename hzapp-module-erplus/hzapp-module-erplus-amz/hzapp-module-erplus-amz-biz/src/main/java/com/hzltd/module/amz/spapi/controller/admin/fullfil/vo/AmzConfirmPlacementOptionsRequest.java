package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;

@Data
public class AmzConfirmPlacementOptionsRequest {

    /**
     * 入仓计划ID
     */
    private String planId;

    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 入仓计划选项ID
     */
    private String placementOptionId;
}
