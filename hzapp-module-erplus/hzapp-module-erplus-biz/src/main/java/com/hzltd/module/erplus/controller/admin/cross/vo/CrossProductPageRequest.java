package com.hzltd.module.erplus.controller.admin.cross.vo;

import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class CrossProductPageRequest extends PageParam{

    /**
     * 跨境平台ID
     */
    private Integer platformId;

     /**
      * 店铺ID
      */
    private Integer shopId;

    /**
     * 区域编码
     */
    private String marketId;

    /**
     * 业务模式
     */
    private Integer fulfillType;

    /**
     * 创建时间范围
     */
    private String[] createTimeRange;

    /**
     * 更新时间范围
     */
    private String [] updateTimeRange;

    /**
     * 卖家SKU编码
     */
    private String sellerSkuCode;

    /**
     * 跨境商品编码
     */
    private String platformProductCode;

    /**
     * 跨境商品状态
     */
    private List<Integer> status;
}
