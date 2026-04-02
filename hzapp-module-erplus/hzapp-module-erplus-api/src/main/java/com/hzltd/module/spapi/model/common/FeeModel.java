package com.hzltd.module.spapi.model.order;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class FeeModel {
    private String identifier;

    private String marketId;

    private String platformProductCode;

    private String sellerSku;

    private String currency;

    private Integer totalFee;

    private List<FeeItem> feeItems;

    public FeeModel addFeeItem(FeeItem feeItem) {
        if (feeItems == null) {
            feeItems = Lists.newArrayList();
        }
        feeItems.add(feeItem);
        return this;
    }

    public Integer getReferralFee() {
        return feeItems.stream()
                .filter(feeItem -> "ReferralFee".equals(feeItem.getFeeType()))
                .map(FeeItem::getFinalFee)
                .findFirst()
                .orElse(0);
    }

    public Integer getShippingFee() {
        return feeItems.stream()
                .filter(feeItem -> "FBAFees".equals(feeItem.getFeeType()))
                .map(FeeItem::getFinalFee)
                .findFirst()
                .orElse(0);
    }

    @Data
    public static class FeeItem {
        private String feeType;

        private String currency;

        private Integer feeAmount;

        private Integer feePromotion;

        private Integer taxAmount;

        private Integer finalFee;
    }
}
