package com.hzltd.module.erplus.dal.mysql.purchase;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.purchase.ErpPurchaseOrderItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * ERP 采购订单明项目 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpPurchaseOrderItemMapper extends BaseMapperX<ErpPurchaseOrderItemDO> {

    default List<ErpPurchaseOrderItemDO> selectListByOrderId(Long orderId) {
        return selectList(ErpPurchaseOrderItemDO::getOrderId, orderId);
    }

    default List<ErpPurchaseOrderItemDO> selectListByOrderIds(Collection<Long> orderIds) {
        return selectList(ErpPurchaseOrderItemDO::getOrderId, orderIds);
    }

    default int deleteByOrderId(Long orderId) {
        return delete(ErpPurchaseOrderItemDO::getOrderId, orderId);
    }

}