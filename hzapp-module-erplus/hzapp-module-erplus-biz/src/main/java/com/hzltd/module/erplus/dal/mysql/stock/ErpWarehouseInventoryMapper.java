package com.hzltd.module.erplus.dal.mysql.stock;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库库存 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpWarehouseInventoryMapper extends BaseMapperX<ErpWarehouseInventoryDO> {

    default ErpWarehouseInventoryDO selectByWarehouseAndItemForUpdate(Long warehouseId, Integer itemType, Long itemId) {
        return selectOne(new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eq(ErpWarehouseInventoryDO::getWarehouseId, warehouseId)
                .eq(ErpWarehouseInventoryDO::getItemType, itemType)
                .eq(ErpWarehouseInventoryDO::getItemId, itemId)
                .last("FOR UPDATE"));
    }

}