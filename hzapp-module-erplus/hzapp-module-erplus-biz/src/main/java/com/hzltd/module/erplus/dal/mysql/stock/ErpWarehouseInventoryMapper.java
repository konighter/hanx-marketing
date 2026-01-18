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

    default ErpWarehouseInventoryDO selectByWarehouseIdAndSkuForUpdate(Long warehouseId, String sku) {
        return selectOne(new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eq(ErpWarehouseInventoryDO::getWarehouseId, warehouseId.intValue())
                .eq(ErpWarehouseInventoryDO::getSellerSku, sku)
                .last("FOR UPDATE"));
    }

}