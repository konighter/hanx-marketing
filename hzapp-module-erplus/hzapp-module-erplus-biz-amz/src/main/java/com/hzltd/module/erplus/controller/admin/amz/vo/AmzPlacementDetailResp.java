package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

import java.util.List;

@Data
public class AmzPlacementDetailResp {

    /**
     * 发货日期
     */
    private String shipmentDate;

    private List<AmzShipment> shipments;

    private AmzPlacementOption placementOption;

    private List<AmzTransportationOption> amzTransportationOptions;

}
