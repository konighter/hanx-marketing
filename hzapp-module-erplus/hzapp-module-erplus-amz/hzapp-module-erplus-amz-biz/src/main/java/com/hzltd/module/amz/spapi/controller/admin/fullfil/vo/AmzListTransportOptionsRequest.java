package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;

@Data
public class AmzListTransportOptionsRequest {
    private Integer shopId;
    private String planId;
    private String placementOperationId;
    private String shipmentId;
    private String cursor;
}
