package com.hzltd.module.erplus.dal.mysql.material;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialStockDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ERP 耗材库存 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpMaterialStockMapper extends BaseMapperX<ErpMaterialStockDO> {

    default ErpMaterialStockDO selectByMaterialIdAndWarehouseId(Long materialId, Long warehouseId) {
        return selectOne(ErpMaterialStockDO::getMaterialId, materialId,
                ErpMaterialStockDO::getWarehouseId, warehouseId);
    }

    default int updateQuantityIncrement(Long id, BigDecimal quantity, boolean negativeEnable) {
        LambdaUpdateWrapper<ErpMaterialStockDO> updateWrapper = new LambdaUpdateWrapper<ErpMaterialStockDO>()
                .eq(ErpMaterialStockDO::getId, id);
        if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            updateWrapper.setSql("quantity = quantity + " + quantity);
        } else if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            if (!negativeEnable) {
                updateWrapper.ge(ErpMaterialStockDO::getQuantity, quantity.abs());
            }
            updateWrapper.setSql("quantity = quantity - " + quantity.abs());
        }
        return update(null, updateWrapper);
    }

    default BigDecimal selectSumByMaterialId(Long materialId) {
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<ErpMaterialStockDO>()
                .select("SUM(quantity) AS sumQuantity")
                .eq("material_id", materialId));
        if (CollUtil.isEmpty(result)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(MapUtil.getDouble(result.get(0), "sumQuantity", 0D));
    }

}
