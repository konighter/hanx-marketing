package com.hzltd.module.erplus.api.amz;

import lombok.Data;

@Data
public class AmzCancelInboundPlanRequest {
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 入仓计划ID
     */
    private String inboundPlanId;
}
