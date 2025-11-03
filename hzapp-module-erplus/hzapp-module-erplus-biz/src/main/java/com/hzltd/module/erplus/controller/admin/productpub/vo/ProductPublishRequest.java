package com.hzltd.module.erplus.controller.admin.productpub.vo;

import com.hzltd.module.erplus.constant.FulfillTypeEnum;
import com.hzltd.module.erplus.constant.LanguageEnum;
import com.hzltd.module.erplus.constant.SaveModeEnum;
import com.hzltd.module.erplus.model.category.CategoryModel;
import com.hzltd.module.erplus.model.common.*;
import com.hzltd.module.erplus.model.logistics.LogisticsModel;
import com.hzltd.module.erplus.dal.dataobject.brand.ProductBrandDO;
import com.hzltd.module.erplus.dal.dataobject.product.ErpProductCategoryDO;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;


@Data
public class ProductPublishRequest {

    @NotNull(message = "电商平台不能为空")
    private CrossPlatformEnum crossPlatform;

    /**
     * 目标市场, 根据目标电商平台确定
     */
    @NotNull(message = "目标市场不能为空")
    private String marketId;

    /**
     * 至少一个
     */
    @Size(min = 1, message = "至少提供一个目标店铺")
    private List<Integer> shopIds;

    /**
     * 保存模式, draft or submit
     */
    private SaveModeEnum saveMode;
    /**
     * 配送模式
     */
    @NotNull
    private FulfillTypeEnum fulfillType;

    /**
     * 是否延迟提交
     */
    private Boolean delaySync;

    /**
     * 期望同步时间
     */
    private DateTime scheduleTime;

    /**
     * 关联的产品
     */
    private Long relatedProductId;

    /**
     * 不为空则需要翻译
     */
    private LanguageEnum language;

    /**
     * 多语种标题
     */
    private Map<LanguageEnum, String> title;

    /**
     * 多语种描述
     */
    private Map<LanguageEnum, String> description;

    /**
     * 电商平台category
     */
    private CategoryModel crossPlatformCategory;

    /**
     * 关联的本地系统的品类
     */
    private ErpProductCategoryDO relatedCategory;

    /**
     * 品牌
     */
    private ProductBrandDO brand;

    /**
     * 商品属性, 根据每个品类填充
     */
    private List<ProductAttributeModel> productAttributes;

    /**
     * 第一张为主图, 其他为轮播图
     */
    @NotNull
    private List<Image> mainImages;

    /**
     * 商品视频
     */
    private Video video;

    /**
     * SKU信息
     */
    @NotNull
    private List<SkuVO> skus;

    /**
     * 商品尺寸图或者模版
     */
    private SizeChartModel sizeChart;

    //------ 包裹信息 ------//
    /**
     * 产品尺寸
     */
    private DimensionModel packageDimensions;

    // ------ 物流信息 --------//

    /**
     * 物流信息, 自配送使用
     */
    private LogisticsModel logistics;

    // ------ 税务信息 ------//
    private VatModel vatModel;

    /**
     * 商品认证/授权信息
     */
    private List<CertificationModel> certifications;
    /**
     * 富媒体信息
     */
    private APlusModel aPlusModel;

}
