package com.hzltd.module.erplus.system.dto;

import lombok.Data;

/**
 * 商品属性 - 采购信息
 */
@Data
public class ProductPurchaseAttrDTO {

    /**
     * 供应商编号
     */
    private Long supplierId;

    /**
     * 采购单价，单位：分
     */
    private Integer purchasePrice;

    /**
     * 最小起订量
     */
    private Integer minOrderQty;

    /**
     * 采购周期(天)
     */
    private Integer purchaseDays;

}
