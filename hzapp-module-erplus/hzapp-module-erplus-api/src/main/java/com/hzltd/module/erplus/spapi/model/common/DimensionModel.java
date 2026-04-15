package com.hzltd.module.erplus.spapi.model.common;

import com.hzltd.module.erplus.spapi.enums.LengthUnitEnum;
import com.hzltd.module.erplus.spapi.enums.WeightUnitEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 维度, 一般用作商品/包装尺寸
 */
@Data
public class DimensionModel {

    private Integer length;

    private Integer width;

    private Integer height;

    private LengthUnitEnum unit;

    private BigDecimal weight;

    private WeightUnitEnum weightUnit;

}
