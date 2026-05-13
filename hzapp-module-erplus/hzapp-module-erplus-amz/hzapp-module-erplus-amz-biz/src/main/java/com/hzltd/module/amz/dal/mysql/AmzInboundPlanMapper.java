package com.hzltd.module.amz.dal.mysql;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.amz.spapi.controller.admin.fullfil.vo.AmzListInboundPlansRequest;
import com.hzltd.module.amz.dal.dataobject.AmzInboundPlanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 平台货件信息 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AmzInboundPlanMapper extends BaseMapperX<AmzInboundPlanDO> {

    default PageResult<AmzInboundPlanDO> selectPage(AmzListInboundPlansRequest request) {
        return this.selectPage(request, new LambdaQueryWrapperX<AmzInboundPlanDO>()
                .eqIfPresent(AmzInboundPlanDO::getShopId, request.getShopId())
                .eqIfPresent(AmzInboundPlanDO::getMarketId, request.getMarketId())
                .eqIfPresent(AmzInboundPlanDO::getPlanId, request.getPlanId()));
    }
}