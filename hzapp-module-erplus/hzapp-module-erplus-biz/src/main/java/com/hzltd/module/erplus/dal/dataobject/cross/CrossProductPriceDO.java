package com.hzltd.module.erplus.dal.dataobject.crossproductprice;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

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
     * 平台费用(预估)
     */
    private Integer estimatedPlatformFee;
    /**
     * 平台费用明细（预估）
     */
    private String estimatedPlatformFeeDetail;


}