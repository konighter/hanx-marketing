package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;

@Data
public class AmzListPackingGroupItemRequest {

    private Integer shopId;

    private String inboundPlanId;

    private String packingGroupId;
}
