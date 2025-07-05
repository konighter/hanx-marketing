package com.hzltd.module.erpls.api.model.common;

import com.hzltd.module.erpls.api.constant.WeightUnitEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeightModel {

    private BigDecimal value;

    private WeightUnitEnum unit;

}
