package com.hzltd.module.erplus.spapi.model.pricing;

import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import lombok.Data;

@Data
public class OfferModel {

    private FulfillTypeEnum fulfillType;

    private Double feedbackRate;

    private Integer feedbackCount;

    private PricingModel landedPrice;

    private PricingModel listingPrice;

    private PricingModel shipPrice;

    private boolean isBuyBox;

    private String sellerId;

}
