package com.hzltd.module.erplus.dal.dataobject.spu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品尺寸信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDimension implements Serializable {

    @Schema(description = "长度", example = "10.0")
    private Double length;

    @Schema(description = "宽度", example = "5.0")
    private Double width;

    @Schema(description = "高度", example = "2.0")
    private Double height;

    @Schema(description = "重量", example = "1.5")
    private Double weight;

    @Schema(description = "商品体积", example = "100.0")
    private Double volume;

    @Schema(description = "长度单位", example = "cm")
    private String unit;

    @Schema(description = "重量单位", example = "kg")
    private String weightUnit;
}
