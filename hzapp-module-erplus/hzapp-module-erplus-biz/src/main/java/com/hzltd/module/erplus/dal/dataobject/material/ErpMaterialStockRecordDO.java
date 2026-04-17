package com.hzltd.module.erplus.dal.dataobject.material;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 耗材库存明细 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_material_stock_record")
@KeySequence("erplus_material_stock_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpMaterialStockRecordDO extends BaseDO {

    /**
     * 编号
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
     * 出入库数量
     *
     * 正数，表示入库；负数，表示出库
     */
    private BigDecimal count;
    /**
     * 总库存量
     *
     * 出入库之后，目前的库存量
     */
    private BigDecimal totalCount;
    /**
     * 业务类型
     *
     * 枚举 ErpStockRecordBizTypeEnum
     */
    private Integer bizType;
    /**
     * 业务编号
     */
    private Long bizId;
    /**
     * 业务项编号
     */
    private Long bizItemId;
    /**
     * 业务单号
     */
    private String bizNo;

}
