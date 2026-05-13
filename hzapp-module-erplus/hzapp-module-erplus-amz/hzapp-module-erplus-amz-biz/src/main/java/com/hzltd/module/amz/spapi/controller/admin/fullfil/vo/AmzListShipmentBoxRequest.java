package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import com.hzltd.module.amz.adv.controller.admin.vo.AmzBaseRequest;
import lombok.Data;

@Data
public class AmzListShipmentBoxRequest extends AmzBaseRequest {

    private String shipmentId;
}
