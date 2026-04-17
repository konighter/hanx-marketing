package com.hzltd.module.erplus.system.model;

import com.hzltd.module.erplus.system.dto.ProductCertificationDTO;
import com.hzltd.module.erplus.system.dto.ProductDimensionDTO;
import com.hzltd.module.erplus.system.dto.ProductMaterialDTO;
import com.hzltd.module.erplus.system.dto.ProductQualityReportDTO;
import com.hzltd.module.erplus.spapi.model.category.BrandModel;
import com.hzltd.module.erplus.spapi.model.category.CategoryModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductSpuModel {
    /**
     * 商品编号
     */
    private Long id;
    // ========== 基本信息 =========

    /**
     * 商品名称
     */
    private String name;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 商品简介
     */
    private List<String> introduction;
    /**
     * 商品详情
     */
    private String description;

    /**
     * 商品分类编号
     */
    private CategoryModel categoryModel;

    /**
     * 跨境平台的分类商品编号
     */
    private String crossCategory;

    /**
     * 品牌编号
     */
    private BrandModel brandModel;


    /**
     * 商品封面图
     */
    private String picUrl;
    /**
     * 商品轮播图
     */
    private List<String> sliderPicUrls;

    /**
     * 规格类型
     *
     * false - 单规格
     * true - 多规格
     */
    private Boolean specType;

    /**
     * 商品变体属性列表
     * 例如：颜色、尺寸、重量等
     * 从SKU中提取的变体属性
     */
    private List<String> variationProperties;

    /**
     * 商品SKU列表
     */
    private List<ProductSkuModel> skuModelList;

    /**
     * 商品属性
     */
    private Map<String, Object> attributes;

    // ========== 物流 相关字段 =========
    private ProductDimensionDTO itemDim;
    private ProductDimensionDTO pkgDim;
    private ProductDimensionDTO boxDim;
    private Integer inboxnum;

    // ========== 合规和资质 相关字段 ==========
    private List<ProductCertificationDTO> certifications;
    private List<String> safetyStandards;
    private List<String> safetyWarnings;
    private List<ProductMaterialDTO> materials;
    private List<String> hazardousSubstances;
    private List<String> environmentalCertifications;
    private List<String> packagingMaterials;
    private List<String> applicableRegulations;
    private List<String> restrictedRegions;
    private List<String> specialLicenses;
    private List<ProductQualityReportDTO> qualityReports;

}
