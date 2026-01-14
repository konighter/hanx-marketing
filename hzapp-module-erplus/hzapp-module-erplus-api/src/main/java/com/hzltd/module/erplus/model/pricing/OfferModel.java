package com.hzltd.module.erplus.model.pricing;

import com.hzltd.module.erplus.constant.FulfillTypeEnum;
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
