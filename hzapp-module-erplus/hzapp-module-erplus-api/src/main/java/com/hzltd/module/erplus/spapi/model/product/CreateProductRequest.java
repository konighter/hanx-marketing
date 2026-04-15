package com.hzltd.module.erplus.spapi.model.product;

import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.spapi.enums.LanguageEnum;
import com.hzltd.module.erplus.spapi.enums.OperationModeEnum;
import com.hzltd.module.erplus.spapi.model.category.BrandModel;
import com.hzltd.module.erplus.spapi.model.category.CategoryModel;
import com.hzltd.module.erplus.spapi.model.common.*;
import com.hzltd.module.erplus.spapi.model.logistics.LogisticsModel;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class CreateProductRequest {

    @NotNull
    private CrossPlatformEnum crossPlatform;

    @NotNull
    private Integer shopId;

    @NotNull
    private String marketId;

    @NotNull
    private OperationModeEnum saveMode;

    @NotNull
    private LanguageEnum language;

    /**
     * 根据授权平台选择
     */
    @NotNull
    private FulfillTypeEnum fulfillType;

    @NotNull
    private String title;

    private String keywords;

    private List<String> features;

    @NotNull
    private String description;

    @NotNull
    private CategoryModel category;

    /**
     * 品牌
     */
    private BrandModel brand;

    @NotNull
    private Image mainImage;

    @NotNull
    private List<Image> sliderImages;

    private Video video;

    /**
     * 多规格：true
     * 单规格：false
     */
    private Boolean specType;

    @Size(min = 1)
    private List<SkuModel> skus;


    private List<CertificationModel> certifications;

    /**
     * 产品尺寸/重量
     */
    private DimensionModel dimension;

    /**
     * 安全合规
     */
    private SecurityModel security;

    /**
     * 物流信息
     */
    private LogisticsModel logistics;

    private List<ProductAttributeModel> productAttributes;

    private Long relateProductId;

    private Map<String, Object> crossPlatformExtAttrs;

    /**
     * 是否预览, 预览不会实际提交到平台, 或者用预览模式请求平台
     */
    private boolean preview = false;

}
