package com.hzltd.module.erplus.dal.dataobject.material;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 耗材库存 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_material_stock")
@KeySequence("erplus_material_stock_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpMaterialStockDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 耗材编号
     *
     * 关联 {@link ErpMaterialDO#getId()}
     */
    private Long materialId;
    /**
     * 仓库编号
     *
     * 关联 {@link ErpWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 当前库存数量
     */
    private BigDecimal quantity;

}
