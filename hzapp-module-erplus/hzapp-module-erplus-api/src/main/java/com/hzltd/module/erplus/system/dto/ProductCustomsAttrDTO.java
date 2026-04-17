package com.hzltd.module.erplus.system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品属性 - 海关报关
 */
@Data
public class ProductCustomsAttrDTO {

    /**
     * 中文报关名
     */
    private String declarationNameCn;

    /**
     * 英文报关名
     */
    private String declarationNameEn;

    /**
     * 中文材质
     */
    private String materialCn;

    /**
     * 英文材质
     */
    private String materialEn;

    /**
     * 中文用途
     */
    private String usageCn;

    /**
     * 英文用途
     */
    private String usageEn;

    /**
     * 品牌类型
     */
    private String brandType;

    /**
     * 出口享惠情况
     */
    private String exportBenefit;

    /**
     * 内部编码
     */
    private String internalCode;

    /**
     * 产品属性/性质
     */
    private List<String> nature;

    /**
     * 报关单价
     */
    private BigDecimal declarationPrice;

    /**
     * 报关币种
     */
    private String declarationCurrency;

    /**
     * 报关 HSCODE
     */
    private String declarationHsCode;

    /**
     * 报关型号
     */
    private String declarationModel;

    /**
     * 原产地(地区)
     */
    private String originCountry;

    /**
     * 境内货源地
     */
    private String domesticSource;

    /**
     * 报关单位
     */
    private String declarationUnit;

    /**
     * 征免
     */
    private String taxExemption;

    /**
     * 申报要素
     */
    private String declarationElement;

    /**
     * 生产销售企业名称
     */
    private String manufacturerName;

    /**
     * 生产销售企业代码
     */
    private String manufacturerCode;

    /**
     * 清关型号
     */
    private String clearanceModel;

    /**
     * 配料备注
     */
    private String ingredientRemark;

    /**
     * 织造方式
     */
    private String weavingMethod;

    /**
     * 清关图片
     */
    private String clearancePicUrl;

    /**
     * 目的国清关费用
     */
    private List<Map<String, Object>> clearanceFees;

    /**
     * 税务申报信息
     */
    private List<Map<String, Object>> taxInfos;

}
