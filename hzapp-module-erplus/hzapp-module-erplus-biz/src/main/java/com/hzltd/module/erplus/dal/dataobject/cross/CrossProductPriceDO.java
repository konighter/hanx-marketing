package com.hzltd.module.erplus.dal.dataobject.cross;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 跨境产品价格 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_product_price")
@KeySequence("erplus_cross_product_price_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossProductPriceDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 产品ID
     */
    private Long productId;

     /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 市场ID
     */
    private String marketId;
    /**
     * 成本价
     */
    private Integer costPrice;
    /**
     * 售价
     */
    private Integer salePrice;
    /**
     * 币种
     */
    private String currency;

    /**
     * 价格生效开始时间
     */
    private Long startAt;

     /**
     * 价格生效开始时间
     */
    private Long endAt;
    /**
     * 平台费用(预估)
     */
    private Integer estimatedPlatformFee;
    /**
     * 平台费用明细（预估）
     */
    private String estimatedPlatformFeeDetail;


}