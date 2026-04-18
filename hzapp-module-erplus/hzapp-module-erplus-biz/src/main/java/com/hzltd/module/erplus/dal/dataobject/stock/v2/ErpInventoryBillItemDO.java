package com.hzltd.module.erplus.dal.dataobject.stock.v2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ERP 库存账单明细表 (Detail) DO
 *
 * @author 翰展科技
 */
@TableName("erplus_inventory_bill_item")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpInventoryBillItemDO extends BaseDO {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 账单ID
     */
    private Long billId;
    /**
     * 物料类型 (1: SKU, 2: Material)
     */
    private Integer itemType;
    /**
     * 物料ID
     */
    private Long itemId;
    /**
     * SKU (可选)
     */
    private String sellerSku;
    /**
     * 变动数量
     */
    private Integer qty;
    /**
     * 期末存
     */
    private Integer snapshotQty;

    // --- 冗余主表字段，仅用于联表查询展示 ---

    @TableField(exist = false)
    private String billCode;
    @TableField(exist = false)
    private Integer type;
    @TableField(exist = false)
    private String refType;
    @TableField(exist = false)
    private String refCode;
    @TableField(exist = false)
    private String fromId;
    @TableField(exist = false)
    private String toId;
    @TableField(exist = false)
    private String remark;
    @TableField(exist = false)
    private LocalDateTime createTime;

}
