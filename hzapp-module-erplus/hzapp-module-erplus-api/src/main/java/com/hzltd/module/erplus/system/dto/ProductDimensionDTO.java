package com.hzltd.module.erplus.system.dto;

import lombok.Data;

/**
 * 商品规格信息 DTO
 */
@Data
public class ProductDimensionDTO {
    /**
     * 长度
     */
    private Double length;
    /**
     * 宽度
     */
    private Double width;
    /**
     * 高度
     */
    private Double height;
    /**
     * 重量
     */
    private Double weight;
    /**
     * 体积
     */
    private Double volume;
    /**
     * 长度单位
     */
    private String unit;
    /**
     * 重量单位
     */
    private String weightUnit;
}
