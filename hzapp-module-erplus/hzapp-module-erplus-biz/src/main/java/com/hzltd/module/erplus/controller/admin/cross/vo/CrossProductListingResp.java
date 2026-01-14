package com.hzltd.module.erplus.controller.admin.cross.vo;

import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductInventoryDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import com.hzltd.module.erplus.model.common.Image;
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

     private Collection<CrossProductPriceDO> price;

}
