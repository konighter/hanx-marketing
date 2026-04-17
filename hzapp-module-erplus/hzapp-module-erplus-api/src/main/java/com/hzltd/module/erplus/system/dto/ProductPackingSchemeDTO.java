package com.hzltd.module.erplus.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品装箱方案 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPackingSchemeDTO {
    /**
     * 方案名称
     */
    private String name;
    /**
     * 单箱数量
     */
    private Integer quantity;
    /**
     * 外箱规格
     */
    private ProductDimensionDTO outerBoxDim;
    /**
     * 包装规格
     */
    private ProductDimensionDTO pkgDim;
    /**
     * 单箱重量
     */
    private Double boxWeight;
    /**
     * 单箱重量单位
     */
    private String boxWeightUnit;
    /**
     * 单品毛重
     */
    private Double grossWeight;
    /**
     * 单品毛重单位
     */
    private String grossWeightUnit;
}
