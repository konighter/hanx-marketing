package com.hzltd.module.erplus.system.dto;

import lombok.Data;

/**
 * 清关费用条目 DTO
 */
@Data
public class ClearanceFeeDTO {
    private String country;
    private Double firstLegFee; // 默认头程费用(含税)
    private String hsCode;      // 清关HS CODE
    private Double unitPrice;   // 清关单价
    private String currency;    // 币种
    private Double taxRate;     // 清关税率
    private String remark;      // 备注
}
