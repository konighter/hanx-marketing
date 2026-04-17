package com.hzltd.module.erplus.service.material;

import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialStockRecordDO;
import com.hzltd.module.erplus.service.material.bo.ErpMaterialStockRecordCreateReqBO;

import java.util.List;

/**
 * 耗材库存明细 Service 接口
 *
 * @author 翰展科技
 */
public interface ErpMaterialStockRecordService {

    /**
     * 创建耗材库存明细
     *
     * @param createReqBO 创建信息
     */
    void createMaterialStockRecord(ErpMaterialStockRecordCreateReqBO createReqBO);

    /**
     * 获得耗材库存明细列表
     *
     * @param materialId 耗材编号
     * @return 耗材库存明细列表
     */
    List<ErpMaterialStockRecordDO> getMaterialStockRecordListByMaterialId(Long materialId);

}
