package com.hzltd.module.erplus.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 商品物流报关信息 DTO
 */
@Data
public class ProductLogisticsDTO {
    // === 基本信息 (物流相关) ===
    private String materialCn;
    private String materialEn;
    private String usageCn;
    private String usageEn;
    private String brandType;
    private String exportBenefit;
    private String internalCode;
    private List<String> nature; // 产品属性: 普货, 含电, 纯电, 液体, 粉末, 膏体, 磁吸, 纺织品

    // === 报关信息 ===
    private Double declarationPrice;
    private String declarationCurrency;
    private String declarationHsCode;
    private String declarationModel;
    private String originCountry;
    private String domesticSource;
    private String declarationUnit;
    private String declarationElement;
    private String taxExemption;
    private String manufacturerName;
    private String manufacturerCode;

    // === 清关信息 ===
    private String clearanceModel;
    private String ingredientRemark;
    private String weavingMethod;
    private String clearancePicUrl;

    // === 列表信息 (JSON 存储) ===
    private List<ClearanceFeeDTO> clearanceFees;
    private List<TaxInfoDTO> taxInfos;
}
