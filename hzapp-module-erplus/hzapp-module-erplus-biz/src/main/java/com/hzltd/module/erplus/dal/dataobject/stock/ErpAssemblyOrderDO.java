package com.hzltd.module.erplus.dal.dataobject.stock;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.dal.dataobject.product.ErpProductDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 装配件订单 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_assembly_order")
@KeySequence("erplus_assembly_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpAssemblyOrderDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 业务单号
     */
    private String no;
    /**
     * 目标 SKU 编号
     *
     * 关联 {@link ErpProductDO#getId()}
     */
    private Long skuId;
    /**
     * 仓库编号
     *
     * 关联 {@link ErpWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 计划生产数量
     */
    private BigDecimal plannedQty;
    /**
     * 实际生产数量
     */
    private BigDecimal actualQty;
    /**
     * 生产批次号
     */
    private String batchNo;
    /**
     * 状态
     *
     * 0-待启动, 1-装配中, 2-已完成, 3-已取消
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
