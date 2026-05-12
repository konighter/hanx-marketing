package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import lombok.Data;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Item;

@Data
public class AmzBoxItem extends Item {

    private Integer boxWidth;
    private Integer boxHeight;
    private Integer boxLength;
    private Integer boxWeight;
    private Integer quantityInBox;
    private Integer boxQuantity;
}
