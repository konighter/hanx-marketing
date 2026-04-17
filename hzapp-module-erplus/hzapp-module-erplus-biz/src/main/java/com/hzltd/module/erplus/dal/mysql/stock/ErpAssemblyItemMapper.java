package com.hzltd.module.erplus.dal.mysql.stock;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ERP 装配件订单耗材明细 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpAssemblyItemMapper extends BaseMapperX<ErpAssemblyItemDO> {

    default List<ErpAssemblyItemDO> selectListByOrderId(Long orderId) {
        return selectList(ErpAssemblyItemDO::getOrderId, orderId);
    }

}
