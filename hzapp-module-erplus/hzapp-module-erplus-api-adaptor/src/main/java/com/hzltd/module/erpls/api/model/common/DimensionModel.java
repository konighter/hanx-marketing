package com.hzltd.module.erpls.api.model.common;

import com.hzltd.module.erpls.api.constant.LengthUnitEnum;
import lombok.Data;

@Data
public class DimensionModel {

    private Integer length;

    private Integer width;

    private Integer height;

    private LengthUnitEnum unit;

}
