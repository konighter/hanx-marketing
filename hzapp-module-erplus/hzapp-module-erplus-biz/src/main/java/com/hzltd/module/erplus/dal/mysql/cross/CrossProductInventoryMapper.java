package com.hzltd.module.erplus.dal.mysql.crossproductinventory;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.crossproductinventory.CrossProductInventoryDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.crossproductinventory.vo.*;

/**
 * 跨境产品库存 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface CrossProductInventoryMapper extends BaseMapperX<CrossProductInventoryDO> {

    default PageResult<CrossProductInventoryDO> selectPage(CrossProductInventoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrossProductInventoryDO>()
                .eqIfPresent(CrossProductInventoryDO::getMarketId, reqVO.getMarketId())
                .eqIfPresent(CrossProductInventoryDO::getShopId, reqVO.getShopId())
                .eqIfPresent(CrossProductInventoryDO::getProductId, reqVO.getProductId())
                .eqIfPresent(CrossProductInventoryDO::getFulfilltype, reqVO.getFulfilltype())
                .eqIfPresent(CrossProductInventoryDO::getFnsku, reqVO.getFnsku())
                .eqIfPresent(CrossProductInventoryDO::getFulfillableQuantity, reqVO.getFulfillableQuantity())
                .eqIfPresent(CrossProductInventoryDO::getInboundWorkingQuantity, reqVO.getInboundWorkingQuantity())
                .eqIfPresent(CrossProductInventoryDO::getInboundShippedQuantity, reqVO.getInboundShippedQuantity())
                .eqIfPresent(CrossProductInventoryDO::getInboundReceivingQuantity, reqVO.getInboundReceivingQuantity())
                .eqIfPresent(CrossProductInventoryDO::getUnfulfillableQuantity, reqVO.getUnfulfillableQuantity())
                .eqIfPresent(CrossProductInventoryDO::getResearchingQuantity, reqVO.getResearchingQuantity())
                .eqIfPresent(CrossProductInventoryDO::getReservedQuantity, reqVO.getReservedQuantity())
                .eqIfPresent(CrossProductInventoryDO::getReservedPendingOrderQuantity, reqVO.getReservedPendingOrderQuantity())
                .eqIfPresent(CrossProductInventoryDO::getReservedTransshippingQuantity, reqVO.getReservedTransshippingQuantity())
                .eqIfPresent(CrossProductInventoryDO::getReservedFcprocessingQuantity, reqVO.getReservedFcprocessingQuantity())
                .betweenIfPresent(CrossProductInventoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CrossProductInventoryDO::getId));
    }

}