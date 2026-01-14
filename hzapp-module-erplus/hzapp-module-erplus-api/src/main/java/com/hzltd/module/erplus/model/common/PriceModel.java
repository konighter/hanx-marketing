package com.hzltd.module.erplus.model.common;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceModel {

    /**
     * 价格
     */
    private BigDecimal amount;

    /**
     * 积分
     */
    private BigDecimal point;



    /**
     * 原价
     */
    private BigDecimal originAmount;

    /**
     * 货币
     */
    private String currency;
    /**
     * 价格类型
     */
    private String type; // B2C, B2B

    /**
     * 生效开始时间
     */
    private Long startAt;

    /**
     * 生效结束时间
     */
    private Long endAt;

}
