package com.hzltd.module.erplus.system.dto;

import lombok.Data;

/**
 * 商品质量报告 DTO
 */
@Data
public class ProductQualityReportDTO {
    /**
     * 报告类型
     */
    private String reportType;
    /**
     * 报告编号
     */
    private String reportNumber;
    /**
     * 签发日期
     */
    private String issueDate;
    /**
     * 有效期至
     */
    private String validUntil;
    /**
     * 签发机构
     */
    private String issuingAuthority;
}
