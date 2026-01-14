package com.hzltd.module.erplus.dal.dataobject.cross;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 商品spu DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_product")
@KeySequence("erplus_cross_product_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpCrossProductDO extends BaseDO {

    /**
     * 商品ID
     */
    @TableId
    private Long id;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 市场ID
     */
    private String marketId;
    /**
     * 配送类型
     */
    private Integer fulfillType;

    /**
     * 电商平台商品ID
     */
    private String platformProductCode;
    /**
     * 关联本地产品ID
     */
    private String sellerSkuCode;
    /**
     * 商品名称
     */
    private String title;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 商品特性
     */
    private String features;
    /**
     * 商品详情
     */
    private String description;
    /**
     * 商品分类编号
     */
    private String categoryId;
    /**
     * 商品品牌编号
     */
    private String brand;
    /**
     * 商品封面图
     */
    private String mainImageUrl;
    /**
     * 商品轮播图地址，数组，以逗号分隔，最多上传15张
     */
    private String sliderImageUrls;
    /**
     * 商品视频
     */
    private String videoUrl;

    /**
     * 尺寸重量
     */
    private String dimension;

    /**
     * 安全合规
     */
//    private String security;
    /**
     * 尺寸图片/模版
     */
    private String sizeChart;
    /**
     * 补充信息
     */
    private String extra;
    /**
     * 规格类型：0 单规格 1 多规格
     */
//    private Boolean specType;
    /**
     * 刊登状态
     */
//    private Integer publishStatus;

    /**
     * 数据版本号
     */
    private Long version;
    /**
     * 商品状态
     */
    private Integer status;

}