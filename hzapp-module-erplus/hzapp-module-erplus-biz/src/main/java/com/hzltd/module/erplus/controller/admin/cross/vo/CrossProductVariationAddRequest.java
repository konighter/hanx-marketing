package com.hzltd.module.erplus.controller.admin.cross.vo;

import com.hzltd.module.erplus.spapi.model.common.ProductAttributeModel;
import lombok.Data;

import java.util.List;

/**
 * spu-sku结构的平台
 */
@Data
public class CrossProductVariationAddRequest {
    /**
     * 跨境商品id
     */
    private Long productId;

    /**
     * 跨境商品sku
     */
    private String sellerSkuCode;

    /**
     * 跨境商品平台skuId
     */
    private String platformSkuId;

    /**
     * 跨境商品属性
     */
    private List<ProductAttributeModel> variationAttributes;

}
