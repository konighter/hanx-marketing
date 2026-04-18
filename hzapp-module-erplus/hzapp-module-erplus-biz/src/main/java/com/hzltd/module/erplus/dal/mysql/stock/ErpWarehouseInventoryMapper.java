package com.hzltd.module.erplus.dal.mysql.stock;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 仓库库存 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpWarehouseInventoryMapper extends BaseMapperX<ErpWarehouseInventoryDO> {

    default ErpWarehouseInventoryDO selectByWarehouseAndItem(Long warehouseId, Integer itemType, Long itemId) {
        return selectOne(new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eq(ErpWarehouseInventoryDO::getWarehouseId, warehouseId)
                .eq(ErpWarehouseInventoryDO::getItemType, itemType)
                .eq(ErpWarehouseInventoryDO::getItemId, itemId));
    }

    default ErpWarehouseInventoryDO selectByWarehouseAndItemForUpdate(Long warehouseId, Integer itemType, Long itemId) {
        return selectOne(new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eq(ErpWarehouseInventoryDO::getWarehouseId, warehouseId)
                .eq(ErpWarehouseInventoryDO::getItemType, itemType)
                .eq(ErpWarehouseInventoryDO::getItemId, itemId)
                .last("FOR UPDATE"));
    }

    /**
     * 批量查询指定仓库、物料类型、物料ID列表的库存
     */
    default List<ErpWarehouseInventoryDO> selectListByWarehouseAndItems(
            Long warehouseId, Integer itemType, Collection<Long> itemIds) {
        return selectList(new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eq(ErpWarehouseInventoryDO::getWarehouseId, warehouseId)
                .eq(ErpWarehouseInventoryDO::getItemType, itemType)
                .in(ErpWarehouseInventoryDO::getItemId, itemIds));
    }

    /**
     * 查询指定物料类型、物料ID在所有仓库的库存汇总
     */
    default List<ErpWarehouseInventoryDO> selectListByItemTypeAndItemIds(
            Integer itemType, Collection<Long> itemIds) {
        return selectList(new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eq(ErpWarehouseInventoryDO::getItemType, itemType)
                .in(ErpWarehouseInventoryDO::getItemId, itemIds));
    }

}