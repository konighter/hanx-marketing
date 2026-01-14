package com.hzltd.module.erplus.dal.mysql.crossorderitem;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.crossorderitem.CrossOrderItemDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.crossorderitem.vo.*;

/**
 * 订单项 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface CrossOrderItemMapper extends BaseMapperX<CrossOrderItemDO> {

    default PageResult<CrossOrderItemDO> selectPage(CrossOrderItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrossOrderItemDO>()
                .eqIfPresent(CrossOrderItemDO::getOrderId, reqVO.getOrderId())
                .eqIfPresent(CrossOrderItemDO::getProductId, reqVO.getProductId())
                .eqIfPresent(CrossOrderItemDO::getPlatformProductCode, reqVO.getPlatformProductCode())
                .eqIfPresent(CrossOrderItemDO::getSellerSkuCode, reqVO.getSellerSkuCode())
                .eqIfPresent(CrossOrderItemDO::getTitle, reqVO.getTitle())
                .eqIfPresent(CrossOrderItemDO::getItemCount, reqVO.getItemCount())
                .eqIfPresent(CrossOrderItemDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(CrossOrderItemDO::getAmount, reqVO.getAmount())
                .eqIfPresent(CrossOrderItemDO::getTotalAmount, reqVO.getTotalAmount())
                .eqIfPresent(CrossOrderItemDO::getShipFee, reqVO.getShipFee())
                .eqIfPresent(CrossOrderItemDO::getCodFee, reqVO.getCodFee())
                .eqIfPresent(CrossOrderItemDO::getPointsNum, reqVO.getPointsNum())
                .eqIfPresent(CrossOrderItemDO::getPointsMoneyValue, reqVO.getPointsMoneyValue())
                .betweenIfPresent(CrossOrderItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CrossOrderItemDO::getId));
    }

}