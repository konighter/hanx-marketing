package com.hzltd.module.erplus.dal.dataobject.stock.v2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * ERP 库存盘点明细表 (Detail) DO
 *
 * @author 翰展科技
 */
@TableName("erplus_inventory_check_item")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpInventoryCheckItemDO extends BaseDO {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 所属盘点任务 ID
     */
    private Long checkId;
    /**
     * SKU
     */
    private String sellerSku;
    /**
     * 账面数量 (时刻快照)
     */
    private Integer bookQty;
    /**
     * 盘点数量 (实清数量)
     */
    private Integer checkQty;
    /**
     * 差异数量
     *
     * 该字段是虚拟生成列，不需要通过 Java 设置
     */
    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER, updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private Integer diffQty;
    /**
     * 关联的库存流水 ID
     */
    private Long adjustBillId;
    /**
     * 备注
     */
    private String remark;

}
