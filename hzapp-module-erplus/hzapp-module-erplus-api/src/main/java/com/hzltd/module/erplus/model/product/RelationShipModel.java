package com.hzltd.module.erplus.model.product;

import com.hzltd.module.erplus.enums.RelationLevel;
import lombok.Data;

import java.util.List;

@Data
public class RelationShipModel {

    /**
     * 关联关系等级
     */
    private RelationLevel relationLevel;

    /**
     * 父商品S
     */
    private String parentProduct;

    /**
     * 父商品SKU
     */
    private String parentSku;

     /**
      * 子商品
      */
    private List<String> childProducts;

    /**
     * 子商品SKU列表
     */
    private List<String> childSkus;

    /**
     * 变体主题
     */
    private String variantTheme;

    /**
     * 变体属性列表
     */
    private List<String> variantAttributes;

}
