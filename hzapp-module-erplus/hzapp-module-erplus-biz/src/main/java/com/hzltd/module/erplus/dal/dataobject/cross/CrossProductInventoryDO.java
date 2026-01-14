package com.hzltd.module.erplus.dal.dataobject.crossproductinventory;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 跨境产品库存 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_product_inventory")
@KeySequence("erplus_cross_product_inventory_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossProductInventoryDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 市场
     */
    private String marketId;
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 配送类型
     */
    private Integer fulfilltype;
    /**
     * 库存SKU
     */
    private String fnsku;
    /**
     * 可用库存
     */
    private Integer fulfillableQuantity;
    /**
     * 待提交库存
     */
    private Integer inboundWorkingQuantity;
    /**
     * 已发货库存
     */
    private Integer inboundShippedQuantity;
    /**
     * 接受中库存
     */
    private Integer inboundReceivingQuantity;
    /**
     * 不可配送库存
     */
    private Integer unfulfillableQuantity;
    /**
     * 调查中库存
     */
    private Integer researchingQuantity;
    /**
     * 预留库存
     */
    private Integer reservedQuantity;
    /**
     * 预留库存-订单
     */
    private Integer reservedPendingOrderQuantity;
    /**
     * 预留库存-调仓
     */
    private Integer reservedTransshippingQuantity;
    /**
     * 预留库存-仓库处理中
     */
    private Integer reservedFcprocessingQuantity;


}