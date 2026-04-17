package com.hzltd.module.erplus.service.material;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialStockDO;

import java.math.BigDecimal;
import java.util.List;

/**
 * ERP 耗材库存 Service 接口
 *
 * @author 翰展科技
 */
public interface ErpMaterialStockService {

    /**
     * 获得耗材库存
     *
     * @param id 编号
     * @return 耗材库存
     */
    ErpMaterialStockDO getMaterialStock(Long id);

    /**
     * 基于耗材 + 仓库，获得耗材库存
     *
     * @param materialId 耗材编号
     * @param warehouseId 仓库编号
     * @return 耗材库存
     */
    ErpMaterialStockDO getMaterialStock(Long materialId, Long warehouseId);

    /**
     * 获得耗材库存数量
     *
     * 如果不存在库存记录，则返回 0
     *
     * @param materialId 耗材编号
     * @return 耗材库存数量
     */
    BigDecimal getMaterialStockCount(Long materialId);

    /**
     * 获得耗材库存数量
     *
     * 如果不存在库存记录，则返回 0
     *
     * @param materialId 耗材编号
     * @param warehouseId 仓库编号
     * @return 耗材库存数量
     */
    BigDecimal getMaterialStockCount(Long materialId, Long warehouseId);

    /**
     * 获得耗材库存分页
     *
     * @return 库存分页
     */
    PageResult<ErpMaterialStockDO> getMaterialStockPage();

    /**
     * 增量更新耗材库存数量
     *
     * @param materialId 耗材编号
     * @param warehouseId 仓库编号
     * @param quantity 增量数量：正数，表示增加；负数，表示减少
     * @return 更新后的库存
     */
    BigDecimal updateMaterialStockCountIncrement(Long materialId, Long warehouseId, BigDecimal quantity);

    /**
     * 批量获取多个耗材的库存
     *
     * @param materialIds 耗材编号列表
     * @return 耗材库存列表
     */
    List<ErpMaterialStockDO> getMaterialStockListByMaterialIds(List<Long> materialIds);

}
