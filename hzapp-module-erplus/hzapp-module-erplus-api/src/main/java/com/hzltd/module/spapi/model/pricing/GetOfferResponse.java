package com.hzltd.module.spapi.model.pricing;

import lombok.Data;

import java.util.List;

@Data
public class GetOfferResponse {

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * 平台产品代码
     */
    private String productCode;

    /**
     * 卖家SKU
     */
    private String sellerSku;

    /**
     * 报价总量
     */
    private Integer totalOfferCount;

    /**
     * 购买框价格
     */
    private OfferModel buyBoxPrice;

    /**
     * 最低价格
     */
    private List<OfferModel> lowestPrice;

    /**
     * 我的报价
     */
    private OfferModel myOffer;

    /**
     * 所有报价
     */
    private List<OfferModel> allOffers;

}
