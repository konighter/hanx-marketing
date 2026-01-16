package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Box;

@Data
public class AmzBox extends Box {

    private String labelUrl;

}
