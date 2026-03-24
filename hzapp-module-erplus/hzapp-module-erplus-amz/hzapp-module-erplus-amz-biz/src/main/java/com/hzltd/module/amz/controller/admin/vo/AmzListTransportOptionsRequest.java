package com.hzltd.module.amz.controller.admin.vo;

import lombok.Data;

@Data
public class AmzListTransportOptionsRequest {
    private Integer shopId;
    private String planId;
    private String placementOperationId;
    private String shipmentId;
    private String cursor;
}
