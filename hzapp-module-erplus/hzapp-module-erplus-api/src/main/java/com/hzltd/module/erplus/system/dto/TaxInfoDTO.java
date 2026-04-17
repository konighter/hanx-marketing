package com.hzltd.module.erplus.system.dto;

import lombok.Data;

/**
 * 税务信息条目 DTO
 */
@Data
public class TaxInfoDTO {
    private String companyName;
    private String cnpj;
    private String ncm;
    private String unit;
    private String origin;
    private String taxType;
}
