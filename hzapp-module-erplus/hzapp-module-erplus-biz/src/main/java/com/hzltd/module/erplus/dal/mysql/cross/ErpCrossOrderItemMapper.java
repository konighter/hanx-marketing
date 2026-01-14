package com.hzltd.module.erplus.dal.mysql.cross;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单项 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossOrderItemMapper extends BaseMapperX<CrossOrderItemDO> {

   default CrossOrderItemDO selectOne(String platformOrderId, Integer platformId) {
        return this.selectOne(new LambdaQueryWrapperX<CrossOrderItemDO>()
                .eqIfPresent(CrossOrderItemDO::getPlatformOrderId, platformOrderId)
                .eqIfPresent(CrossOrderItemDO::getPlatformId, platformId));
    }

    default List<CrossOrderItemDO> selectListByOrderIds(List<Long> orderIds) {
        return this.selectList(new LambdaQueryWrapperX<CrossOrderItemDO>()
                .inIfPresent(CrossOrderItemDO::getOrderId, orderIds));
    }

}