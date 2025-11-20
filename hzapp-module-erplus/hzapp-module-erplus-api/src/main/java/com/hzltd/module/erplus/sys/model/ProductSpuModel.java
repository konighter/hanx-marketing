package com.hzltd.module.erplus.sys.model;

import com.hzltd.module.erplus.model.category.BrandModel;
import com.hzltd.module.erplus.model.category.CategoryModel;
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

}
