package com.hzltd.module.erpls.api.model.common;

import com.hzltd.module.erpls.api.constant.LengthUnitEnum;
import com.hzltd.module.erpls.api.constant.WeightUnitEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DimensionModel {

    private Integer length;

    private Integer width;

    private Integer height;

    private LengthUnitEnum unit;

    private BigDecimal weight;

    private WeightUnitEnum weightUnit;

}
