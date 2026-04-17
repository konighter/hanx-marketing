package com.hzltd.module.erplus.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 商品属性 - 物流规格
 */
@Data
public class ProductLogisticsAttrDTO {

    /**
     * 商品重量,单重，单位：kg 千克
     */
    private Double weight;

    /**
     * 商品体积,单位：m^3 立方米
     */
    private Double volume;

    /**
     * 净品规格
     */
    private ProductDimensionDTO itemDim;

    /**
     * 包裹规格
     */
    private ProductDimensionDTO pkgDim;

    /**
     * 单箱规格
     */
    private ProductDimensionDTO boxDim;

    /**
     * 单箱数量
     */
    private Integer inboxnum;

    /**
     * 装箱方案
     */
    private List<ProductPackingSchemeDTO> packingSchemes;

}
