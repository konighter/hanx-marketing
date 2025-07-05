package com.hzltd.module.erpls.api.model.common;

import com.hzltd.module.erpls.api.constant.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceModel {

    private BigDecimal amount;

    private BigDecimal originAmount;

    private CurrencyEnum currency;

}
