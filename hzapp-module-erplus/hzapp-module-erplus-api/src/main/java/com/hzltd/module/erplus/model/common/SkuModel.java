package com.hzltd.module.erplus.model.common;

import lombok.Data;

import java.util.List;


@Data
public class SkuModel {

    /**
     * 平台的SKUID, AMZ是ASIN
     */
    private String skuId;

    private String sellerSku;

    private String externalSkuId;

    private List<SkuAttribute> salesAttributes;

    private List<InventoryModel> inventory;

    private PriceModel price;

    private IdentifierCodeModel identifierCode;

    private Image image;
}
