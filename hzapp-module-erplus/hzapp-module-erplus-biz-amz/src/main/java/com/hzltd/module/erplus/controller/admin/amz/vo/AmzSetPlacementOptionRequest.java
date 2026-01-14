package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

import java.util.List;

@Data
public class AmzSetPlacementOptionRequest {
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 入仓计划ID
     */
    private String planId;
    /**
     * 入仓计划选项ID
     */
    private String placementOptionId;
    /**
     * 入仓计划选项中的货件列表
     */
    private List<AmzShipment> shipments;
}
