package com.hzltd.module.erplus.model.order;

import com.hzltd.module.erplus.constant.FulfillTypeEnum;
import lombok.Data;

@Data
public class ProductFeeEstimateRequest {
    private String platformProductCode;

    private String sellerSku;

    private String currency;

    private Integer price;

    private FulfillTypeEnum fulfillType;
}
