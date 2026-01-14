package com.hzltd.module.erplus.dal.mysql.shipmentitem;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.shipmentitem.ShipmentItemDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.shipmentitem.vo.*;

/**
 * 货件详情 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ShipmentItemMapper extends BaseMapperX<ShipmentItemDO> {

    default PageResult<ShipmentItemDO> selectPage(ShipmentItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ShipmentItemDO>()
                .eqIfPresent(ShipmentItemDO::getShipmentId, reqVO.getShipmentId())
                .eqIfPresent(ShipmentItemDO::getSellerSku, reqVO.getSellerSku())
                .eqIfPresent(ShipmentItemDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(ShipmentItemDO::getLength, reqVO.getLength())
                .eqIfPresent(ShipmentItemDO::getWidth, reqVO.getWidth())
                .eqIfPresent(ShipmentItemDO::getHeigth, reqVO.getHeigth())
                .eqIfPresent(ShipmentItemDO::getUnit, reqVO.getUnit())
                .eqIfPresent(ShipmentItemDO::getWeight, reqVO.getWeight())
                .eqIfPresent(ShipmentItemDO::getWeightUnit, reqVO.getWeightUnit())
                .orderByDesc(ShipmentItemDO::getId));
    }

}