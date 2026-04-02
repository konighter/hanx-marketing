package com.hzltd.module.spapi.model.product;

import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.spapi.model.common.PriceModel;
import lombok.Data;

@Data
public class ProductFeeRequest {

    private String marketplaceId;

    private String platformProductCode;

    private FulfillTypeEnum fulfillType;

    private PriceModel price;

    private PriceModel shipFee;
}
