package com.hzltd.module.erplus.dal.mysql.shipmenthis;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.shipmenthis.ShipmentHisDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.shipmenthis.vo.*;

/**
 * 货件状态历史 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ShipmentHisMapper extends BaseMapperX<ShipmentHisDO> {

    default PageResult<ShipmentHisDO> selectPage(ShipmentHisPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ShipmentHisDO>()
                .eqIfPresent(ShipmentHisDO::getShipmentId, reqVO.getShipmentId())
                .eqIfPresent(ShipmentHisDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ShipmentHisDO::getStatusTime, reqVO.getStatusTime())
                .eqIfPresent(ShipmentHisDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ShipmentHisDO::getOperator, reqVO.getOperator())
                .betweenIfPresent(ShipmentHisDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ShipmentHisDO::getId));
    }

}