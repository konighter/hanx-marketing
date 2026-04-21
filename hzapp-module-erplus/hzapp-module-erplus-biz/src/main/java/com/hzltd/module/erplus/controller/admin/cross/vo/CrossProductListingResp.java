package com.hzltd.module.erplus.controller.admin.cross.vo;

import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductInventoryDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import com.hzltd.module.erplus.spapi.model.common.Image;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Data
public class CrossProductListingResp {

    private Long id;

    private String platformProductCode;

    private String sellerProductCode;

    private Integer fulfillType;

    private String fulfillTypeName;

    private String regionCode;

    private String regionName;

    private Integer platformId;
    
    private Integer shopId;

    private String platformName;

    private Image mainImage;

    private List<Image> images;

    private String title;

    private String categoryName;

    /**
     * 跨境商品分类编码
     * 亚马逊的productTYpe
     * 其他平台的category
     */
    private String categoryCode;

     /**
      * 商品品牌
      */
    private String brand;

    private Integer status;

    private String statusName;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

     private CrossProductInventoryDO inventory;

     private Collection<CrossProductPriceDO> prices;

    /**
     * 刊登状态: 0-待发布, 1-发布中, 2-发布成功, 3-发布失败
     */
    private Integer syncStatus;

    /**
     * 刊登状态名称
     */
    private String syncStatusName;

    /**
     * 最新刊登任务 ID
     */
    private Long latestTaskId;

    /**
     * 素材健康分
     */
    private ListingDiagnosisDTO diagnosis;

    /**
     * 销售表现
     */
    private ListingPerformanceDTO performance;

    /**
     * 变体信息
     */
    private List<ListingVariantDTO> variants;

}
