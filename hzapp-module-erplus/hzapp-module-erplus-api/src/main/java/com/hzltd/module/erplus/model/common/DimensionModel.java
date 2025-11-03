package com.hzltd.module.erplus.model.common;

import com.hzltd.module.erplus.constant.LengthUnitEnum;
import com.hzltd.module.erplus.constant.WeightUnitEnum;
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
