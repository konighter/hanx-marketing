package com.hzltd.module.erplus.spapi.model.order;

import lombok.Data;

@Data
public class OrderItemModel {

    private String orderId;

    private String platformProductCode;

    private String sellerSku;

    private String title;

    private int quantity;

    private String currency;

    private int itemPrice;

    private int itemTax;

    private int shipFee;

    private int shipFeeTax;

    private int shipFeeDiscount;

    private int shipFeeDiscountTax;

    private int promoDiscount;

    private int promoDiscountTax;

    private int codFee;

    private int codFeeDiscount;

    private int pointsNum;

    private int pointsAsMoney;


    public int getTotalPrice() {
        return itemPrice + itemTax + shipFee + shipFeeTax - shipFeeDiscount + shipFeeDiscountTax - promoDiscount + promoDiscountTax + codFee - codFeeDiscount;
    }

}
