package com.hzltd.module.erplus.dal.mysql.crossorder;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.crossorder.CrossOrderDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.crossorder.vo.*;

/**
 * 订单 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface CrossOrderMapper extends BaseMapperX<CrossOrderDO> {

    default PageResult<CrossOrderDO> selectPage(CrossOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrossOrderDO>()
                .eqIfPresent(CrossOrderDO::getPlatformId, reqVO.getPlatformId())
                .eqIfPresent(CrossOrderDO::getShopId, reqVO.getShopId())
                .eqIfPresent(CrossOrderDO::getMarketId, reqVO.getMarketId())
                .eqIfPresent(CrossOrderDO::getFulfillType, reqVO.getFulfillType())
                .eqIfPresent(CrossOrderDO::getPlatformOrderId, reqVO.getPlatformOrderId())
                .eqIfPresent(CrossOrderDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(CrossOrderDO::getAmount, reqVO.getAmount())
                .eqIfPresent(CrossOrderDO::getItemCount, reqVO.getItemCount())
                .eqIfPresent(CrossOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CrossOrderDO::getOrderType, reqVO.getOrderType())
                .eqIfPresent(CrossOrderDO::getSaleChannel, reqVO.getSaleChannel())
                .eqIfPresent(CrossOrderDO::getIsPrime, reqVO.getIsPrime())
                .eqIfPresent(CrossOrderDO::getIsBussness, reqVO.getIsBussness())
                .betweenIfPresent(CrossOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CrossOrderDO::getId));
    }

}