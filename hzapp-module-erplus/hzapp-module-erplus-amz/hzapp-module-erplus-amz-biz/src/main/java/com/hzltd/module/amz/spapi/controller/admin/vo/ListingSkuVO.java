package com.hzltd.module.amz.spapi.controller.admin.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ListingSkuVO {


    private Long id;

    /**
     * 跨境平台的商品ID, 如亚马逊的ASIN
     */
    private String platformProductCode;

    private String picUrl;

    private String barCode;

    private BigDecimal price;

    private BigDecimal marketPrice;

    private BigDecimal costPrice;

    private BigDecimal stock;

    private Map<String, Object> attrValues;


}
