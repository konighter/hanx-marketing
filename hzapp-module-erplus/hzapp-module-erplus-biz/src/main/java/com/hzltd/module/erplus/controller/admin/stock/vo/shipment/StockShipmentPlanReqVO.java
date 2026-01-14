package com.hzltd.module.erplus.controller.admin.stock.vo.shipment;

import lombok.Data;

import java.util.List;

@Data
public class StockShipmentPlanReqVO {

    private Integer id;

    private String name;

    private String code;

    private Integer shopId;

    private Integer platformId;

    private Long warehouseId;

    private String destination;

    private Integer fromAddress;

    private String remark;

    private List<ShipmentItemVO> items;

}
