package com.hzltd.module.erplus.spapi.model.order;

import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import lombok.Data;

@Data
public class ProductFeeEstimateRequest {
    private String platformProductCode;

    private String sellerSku;

    private String currency;

    private Integer price;

    private FulfillTypeEnum fulfillType;
}
