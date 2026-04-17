package com.hzltd.module.erplus.service.material.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 耗材库存明细创建 Request BO
 *
 * @author 翰展科技
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErpMaterialStockRecordCreateReqBO {

    /**
     * 耗材编号
     */
    private Long materialId;
    /**
     * 仓库编号
     */
    private Long warehouseId;
    /**
     * 出入库数量
     */
    private BigDecimal count;
    /**
     * 业务类型
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
