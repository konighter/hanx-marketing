package com.hzltd.module.amz.api.spapi.model;

import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import lombok.Data;

@Data
public class AmzFeeEstimateRequest {

   private String platformProductCode;

   private String sellerSku;

   private String currency;

   private Integer price;

   private FulfillTypeEnum fulfillType;

}
