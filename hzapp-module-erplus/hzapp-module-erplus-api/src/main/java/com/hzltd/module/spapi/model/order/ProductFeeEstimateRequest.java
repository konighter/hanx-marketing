package com.hzltd.module.spapi.model.order;

import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import lombok.Data;

@Data
public class ProductFeeEstimateRequest {
    private String platformProductCode;

    private String sellerSku;

    private String currency;

    private Integer price;

    private FulfillTypeEnum fulfillType;
}
