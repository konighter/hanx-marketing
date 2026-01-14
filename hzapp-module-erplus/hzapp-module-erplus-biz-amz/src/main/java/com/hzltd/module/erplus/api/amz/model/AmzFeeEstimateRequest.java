package com.hzltd.module.erplus.api.amz.model;

import com.hzltd.module.erplus.constant.FulfillTypeEnum;
import lombok.Data;

@Data
public class AmzFeeEstimateRequest {

   private String platformProductCode;

   private String sellerSku;

   private String currency;

   private Integer price;

   private FulfillTypeEnum fulfillType;

}
