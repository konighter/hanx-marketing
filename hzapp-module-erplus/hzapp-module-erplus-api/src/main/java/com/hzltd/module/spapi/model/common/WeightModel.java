package com.hzltd.module.spapi.model.common;

import com.hzltd.module.spapi.enums.WeightUnitEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeightModel {

    private BigDecimal value;

    private WeightUnitEnum unit;

}
