package com.hzltd.module.erplus.controller.admin.stock.vo.shipment;

import com.hzltd.module.erplus.enums.AuditAction;
import lombok.Data;

@Data
public class ShipmentAuditReqVO {

    private Integer shipmentId;

    private AuditAction action;

    private String remark;


    public void setAction(Integer action) {
        this.action = AuditAction.of(action);
    }

    public Integer getAction() {
        return action.getValue();
    }

}
