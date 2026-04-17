package com.hzltd.module.erplus.dal.dataobject.spu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品质量报告
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQualityReport implements Serializable {
    @Schema(description = "报告类型", example = "质量检测报告")
    private String reportType;
    @Schema(description = "报告编号", example = "QR202312001")
    private String reportNumber;
    @Schema(description = "签发日期", example = "2023-12-01")
    private String issueDate;
    @Schema(description = "有效期至", example = "2024-12-01")
    private String validUntil;
    @Schema(description = "签发机构", example = "SGS")
    private String issuingAuthority;
}
