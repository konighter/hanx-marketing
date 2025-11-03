package com.hzltd.module.erplus.model.common;

import com.hzltd.module.erplus.constant.WeightUnitEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeightModel {

    private BigDecimal value;

    private WeightUnitEnum unit;

}
