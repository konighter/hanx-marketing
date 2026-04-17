package com.hzltd.module.erplus.dal.dataobject.stock;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 装配件订单耗材明细 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_assembly_item")
@KeySequence("erplus_assembly_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpAssemblyItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 关联装配单编号
     *
     * 关联 {@link ErpAssemblyOrderDO#getId()}
     */
    private Long orderId;
    /**
     * 耗材编号
     *
     * 关联 {@link ErpMaterialDO#getId()}
     */
    private Long materialId;
    /**
     * 应耗数量
     */
    private BigDecimal expectedQty;
    /**
     * 缺料数量
     */
    private BigDecimal shortfallQty;

}
