package com.hzltd.module.erplus.dal.mysql.cross;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderPageRequest;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossOrderMapper extends BaseMapperX<CrossOrderDO> {

    default PageResult<CrossOrderDO> selectPage(CrossOrderPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrossOrderDO>()
                .eqIfPresent(CrossOrderDO::getPlatformId, reqVO.getPlatformId())
                .eqIfPresent(CrossOrderDO::getShopId, reqVO.getShopId())
                .eqIfPresent(CrossOrderDO::getMarketId, reqVO.getMarketId())
                .eqIfPresent(CrossOrderDO::getFulfillType, reqVO.getFulfillType())
                .eqIfPresent(CrossOrderDO::getPlatformOrderId, reqVO.getPlatformOrderId())
                .eqIfPresent(CrossOrderDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CrossOrderDO::getCreateTime, reqVO.getCreateTimeRange())
                .betweenIfPresent(CrossOrderDO::getUpdateTime, reqVO.getUpdateTimeRange())
                .orderByDesc(CrossOrderDO::getCreateTime));
    }

}