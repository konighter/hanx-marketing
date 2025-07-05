package com.hzltd.module.erpls.api.model.product;

import com.hzltd.module.erpls.api.constant.FulfillTypeEnum;
import com.hzltd.module.erpls.api.constant.LanguageEnum;
import com.hzltd.module.erpls.api.constant.SaveModeEnum;
import com.hzltd.module.erpls.api.model.category.CategoryModel;
import com.hzltd.module.erpls.api.model.common.*;
import com.hzltd.module.erpls.api.model.logistics.LogisticsModel;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;


@Data
public class CreateProductRequest {

    @NotNull
    private CrossPlatformEnum crossPlatform;

    @NotNull
    private SaveModeEnum saveMode;

    @NotNull
    private LanguageEnum language;

    /**
     * 根据授权平台选择
     */
    @NotNull
    private FulfillTypeEnum fulfillType;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private CategoryModel category;


    private BrandModel brand;

    @NotNull
    private Image mainImage;

    @NotNull
    private List<Image> sliderImages;

    @Size(min = 1)
    private List<SkuModel> skus;


    private List<CertificationModel> certifications;

    /**
     * 产品尺寸
     */
    private DimensionModel packageDimensions;

    /**
     * 产品重量
     */
    private WeightModel packageWeight;

    /**
     * 安全合规
     */
    private SecurityModel security;

    /**
     * 物流信息
     */
    private LogisticsModel logistics;

    private List<ProductAttributeModel> productAttributes;


    private Video video;

    private String externalProductId;


    private Map<String, Object> crossPlatformExtAttrs;

}
