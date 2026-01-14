package com.hzltd.module.erplus.dal.mysql.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.StockShipmentPlanPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentPlanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 货件 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ShipmentPlanMapper extends BaseMapperX<ShipmentPlanDO> {


    default PageResult<ShipmentPlanDO> selectPage(StockShipmentPlanPageReqVO reqVO) {

        return selectPage(reqVO, new LambdaQueryWrapperX<ShipmentPlanDO>()
                .eqIfPresent(ShipmentPlanDO::getShopId, reqVO.getShopId())
                .betweenIfPresent(ShipmentPlanDO::getCreateTime, reqVO.getCreateDateRange())
                .eqIfPresent(ShipmentPlanDO::getStatus, reqVO.getStatus())
                .orderByDesc(ShipmentPlanDO::getId));

    }

}