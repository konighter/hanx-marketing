package com.hzltd.module.erplus.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 商品属性 - 合规资质
 */
@Data
public class ProductComplianceAttrDTO {

    /**
     * 认证信息
     */
    private List<ProductCertificationDTO> certifications;

    /**
     * 安全标准
     */
    private List<String> safetyStandards;

    /**
     * 安全警告
     */
    private List<String> safetyWarnings;

    /**
     * 材料信息
     */
    private List<ProductMaterialDTO> materials;

    /**
     * 有害物质
     */
    private List<String> hazardousSubstances;

    /**
     * 环保认证
     */
    private List<String> environmentalCertifications;

    /**
     * 包装材料
     */
    private List<String> packagingMaterials;

    /**
     * 适用法规
     */
    private List<String> applicableRegulations;

    /**
     * 限制地区
     */
    private List<String> restrictedRegions;

    /**
     * 特殊许可证
     */
    private List<String> specialLicenses;

    /**
     * 质量报告
     */
    private List<ProductQualityReportDTO> qualityReports;

    /**
     * 质检准则说明
     */
    private String qcStandards;

    /**
     * 内部质检备注
     */
    private String qcRemark;

}
