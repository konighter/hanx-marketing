package com.hzltd.module.erplus.controller.admin.productpotential.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 成本预估方案
 */
@Data
public class FinanceVO {
    private BigDecimal targetPrice;     // 目标售价
    private BigDecimal purchasePrice;   // 采购价
    private BigDecimal freight;         // 运费
    private BigDecimal commissionRate;  // 平台佣金率（% 或 小数，根据后端约定）
    private BigDecimal fbaCost;         // FBA费用
    private BigDecimal cpc;             // 预估CPC
    private BigDecimal targetAcos;      // 目标ACOS
    private BigDecimal gpm;             // 毛利率(估算)
    private BigDecimal npm;             // 净利率(%)
}
