package com.hzltd.module.erplus.dal.dataobject.stock;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 仓库库存 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_warehouse_inventory")
@KeySequence("erplus_warehouse_inventory_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpWarehouseInventoryDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 仓库ID
     */
    private Long warehouseId;
    /**
     * 物料类型 (1: SKU, 2: 耗材)
     */
    private Integer itemType;
    /**
     * 物料ID
     */
    private Long itemId;
    /**
     * 站点ID (仅对SKU有效)
     */
    private Integer siteId;
    /**
     * SKU (可选，冗余展示)
     */
    private String sellerSku;
    /**
     * 总量
     */
    private Integer totalCount;
    /**
     * 可用库存
     */
    private Integer availableCount;
    /**
     * 在途数量 (调拨发货后，确认收货前)
     */
    private Integer transitCount;
    /**
     * 预留量
     */
    private Integer reservedCount;
    /**
     * 挂起量
     */
    private Integer blockCount;

    // --- 虚字段 (仅用于展示) ---

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String image;

}