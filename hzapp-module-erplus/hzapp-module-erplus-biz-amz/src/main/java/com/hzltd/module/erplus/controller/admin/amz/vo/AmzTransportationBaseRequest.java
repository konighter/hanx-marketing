package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

@Data
public class AmzTransportationBaseRequest extends AmzBaseRequest {

    private Integer shopId;

    private String planId;

    private String placementOperationId;

    private String shipmentId;

    private String cursor;

}
