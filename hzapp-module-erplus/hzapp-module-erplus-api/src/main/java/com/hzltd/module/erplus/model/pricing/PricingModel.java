package com.hzltd.module.erplus.model.pricing;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingModel {

    private String currency;

    private BigDecimal amount;

}
