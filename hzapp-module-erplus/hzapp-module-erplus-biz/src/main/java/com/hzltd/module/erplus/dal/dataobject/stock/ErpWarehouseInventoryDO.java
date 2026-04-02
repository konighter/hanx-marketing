package com.hzltd.module.erplus.dal.dataobject.stock;

import com.baomidou.mybatisplus.annotation.KeySequence;
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
@KeySequence("erplus_warehouse_inventory_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
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
    private Integer id;
    /**
     * 仓库ID
     */
    private Integer warehouseId;
    /**
     * 站点ID
     */
    private Integer siteId;
    /**
     * SKU
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
     * 预留量
     */
    private Integer reservedCount;
    /**
     * 挂起量
     */
    private Integer blockCount;


}