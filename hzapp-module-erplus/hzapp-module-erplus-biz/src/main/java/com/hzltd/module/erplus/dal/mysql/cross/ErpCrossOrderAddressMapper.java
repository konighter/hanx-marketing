package com.hzltd.module.erplus.dal.mysql.cross;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderAddressDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 跨境订单收货地址 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossOrderAddressMapper extends BaseMapperX<CrossOrderAddressDO> {

    default CrossOrderAddressDO selectByOrderId(Long orderId) {
        return selectOne(CrossOrderAddressDO::getOrderId, orderId);
    }

    default CrossOrderAddressDO selectByPlatformOrderId(String platformOrderId) {
        return selectOne(CrossOrderAddressDO::getPlatformOrderId, platformOrderId);
    }

}
