package com.hzltd.module.erplus.service.material;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialStockDO;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialStockMapper;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialStockRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;

/**
 * ERP 耗材库存 Service 实现类
 *
 * 维护耗材在各个仓库的实物库存数量，支持增量更新及负库存校验。
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpMaterialStockServiceImpl implements ErpMaterialStockService {

    /**
     * 是否允许负库存（全局开关，目前为不允许）
     */
    private static final Boolean NEGATIVE_STOCK_COUNT_ENABLE = false;

    @Resource
    private ErpMaterialStockMapper materialStockMapper;

    @Override
    public ErpMaterialStockDO getMaterialStock(Long id) {
        return materialStockMapper.selectById(id);
    }

    @Override
    public ErpMaterialStockDO getMaterialStock(Long materialId, Long warehouseId) {
        // 根据耗材和仓库维度定位唯一的库存记录
        return materialStockMapper.selectByMaterialIdAndWarehouseId(materialId, warehouseId);
    }

    @Override
    public BigDecimal getMaterialStockCount(Long materialId) {
        // 计算该耗材在所有仓库的总库存量
        BigDecimal quantity = materialStockMapper.selectSumByMaterialId(materialId);
        return quantity != null ? quantity : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getMaterialStockCount(Long materialId, Long warehouseId) {
        // 获取特定仓库的库存量
        ErpMaterialStockDO stock = getMaterialStock(materialId, warehouseId);
        return stock != null ? stock.getQuantity() : BigDecimal.ZERO;
    }

    @Override
    public PageResult<ErpMaterialStockDO> getMaterialStockPage() {
        // 分页查询库存概览
        return materialStockMapper.selectPage(new com.hzltd.framework.common.pojo.PageParam(), 
                new LambdaQueryWrapperX<ErpMaterialStockDO>()
                        .orderByDesc(ErpMaterialStockDO::getId));
    }

    @Override
    public BigDecimal updateMaterialStockCountIncrement(Long materialId, Long warehouseId, BigDecimal quantity) {
        // 1. 获取当前库存记录，若不存在则初始化
        ErpMaterialStockDO stock = materialStockMapper.selectByMaterialIdAndWarehouseId(materialId, warehouseId);
        if (stock == null) {
            stock = new ErpMaterialStockDO()
                    .setMaterialId(materialId)
                    .setWarehouseId(warehouseId)
                    .setQuantity(BigDecimal.ZERO);
            materialStockMapper.insert(stock);
        }
        
        // 2. 负库存校验逻辑
        if (!NEGATIVE_STOCK_COUNT_ENABLE && stock.getQuantity().add(quantity).compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("耗材库存不足，核减失败");
        }

        // 3. 执行原子增量更新（由 Mapper 层的 SQL `quantity = quantity + #{inc}` 保证多线程安全）
        materialStockMapper.updateQuantityIncrement(stock.getId(), quantity, NEGATIVE_STOCK_COUNT_ENABLE);
        
        // 4. 返回更新后的最新数量
        return getMaterialStockCount(materialId, warehouseId);
    }

    @Override
    public List<ErpMaterialStockDO> getMaterialStockListByMaterialIds(List<Long> materialIds) {
        // 批量获取指定耗材的库存列表
        return materialStockMapper.selectList(
                new LambdaQueryWrapperX<ErpMaterialStockDO>()
                        .in(ErpMaterialStockDO::getMaterialId, materialIds));
    }

}
