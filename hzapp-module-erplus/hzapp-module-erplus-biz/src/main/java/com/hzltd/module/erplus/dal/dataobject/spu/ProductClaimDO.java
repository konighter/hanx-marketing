package com.hzltd.module.erplus.dal.dataobject.spu;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 商品认领 DO
 *
 * @author hzadd
 */
@TableName("ov_product_claim")
@KeySequence("ov_product_claim_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductClaimDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 商品ID
     */
    private Long spuId;
    /**
     * sku信息
     */
    private String skuInfo;
    /**
     * 变种类型
     */
    private Boolean specType;
    /**
     * 平台类型
     */
    private Integer platform;
    /**
     * 语言
     */
    private String language;
    /**
     * 站点
     */
    private Integer sellZone;

    /**
     * 店铺
     */
    private Integer shopId;
    /**
     * 品类
     */
    private String category;
    /**
     * 品牌
     */
    private Integer brandId;
    /**
     * 售价，多变种填最大值
     */
    private Integer sellPrice;
    /**
     * 币种
     */
    private String currency;
    /**
     * 扩展信息
     */
    private String extra;
    /**
     * 状态， 0-认领， 1-已同步， 9-同步失败
     */
    private Integer status;

}