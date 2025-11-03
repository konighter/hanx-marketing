package com.hzltd.module.erplus.model.common;

import com.hzltd.module.erplus.constant.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceModel {

    private BigDecimal amount;

    private BigDecimal originAmount;

    private CurrencyEnum currency;

}
