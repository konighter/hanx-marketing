package com.hzltd.module.erplus.controller.admin.cross.vo;

import com.hzltd.module.erplus.model.common.ProductAttributeModel;

import java.util.List;
import java.util.Map;

public class CrossProductVariationMergeRequest {

    private Long parentProductId;
     /**
      * 跨境商品id
      */
    private List<Long> productId;

    /**
     * 跨境商品属性合并主题(亚马逊)
     */
    private String variationTheme;
     /**
      * 跨境商品属性
      */
    private Map<Long, List<ProductAttributeModel>> variationAttributes;


}
