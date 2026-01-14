package com.hzltd.module.erplus.dal.mysql.shipmentplan;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.shipmentplan.ShipmentPlanDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.shipmentplan.vo.*;

/**
 * 货件 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ShipmentPlanMapper extends BaseMapperX<ShipmentPlanDO> {

    default PageResult<ShipmentPlanDO> selectPage(ShipmentPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ShipmentPlanDO>()
                .likeIfPresent(ShipmentPlanDO::getName, reqVO.getName())
                .eqIfPresent(ShipmentPlanDO::getCode, reqVO.getCode())
                .eqIfPresent(ShipmentPlanDO::getPlatformId, reqVO.getPlatformId())
                .eqIfPresent(ShipmentPlanDO::getShopId, reqVO.getShopId())
                .eqIfPresent(ShipmentPlanDO::getDestination, reqVO.getDestination())
                .eqIfPresent(ShipmentPlanDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(ShipmentPlanDO::getAddress, reqVO.getAddress())
                .eqIfPresent(ShipmentPlanDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ShipmentPlanDO::getExtralId, reqVO.getExtralId())
                .eqIfPresent(ShipmentPlanDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ShipmentPlanDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(ShipmentPlanDO::getSkuCount, reqVO.getSkuCount())
                .eqIfPresent(ShipmentPlanDO::getTotalWeight, reqVO.getTotalWeight())
                .betweenIfPresent(ShipmentPlanDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ShipmentPlanDO::getId));
    }

}