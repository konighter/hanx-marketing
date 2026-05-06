package com.hzltd.module.erplus.adv.dal.mysql.automation;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.automation.controller.admin.vo.AdsAutomationPlanPageReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告自动化计划 Mapper
 */
@Mapper
public interface AdsAutomationPlanMapper extends BaseMapperX<AdsAutomationPlanDO> {

    default PageResult<AdsAutomationPlanDO> selectPage(AdsAutomationPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsAutomationPlanDO>()
                .likeIfPresent(AdsAutomationPlanDO::getName, reqVO.getName())
                .eqIfPresent(AdsAutomationPlanDO::getShopId, reqVO.getShopId())
                .eqIfPresent(AdsAutomationPlanDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AdsAutomationPlanDO::getSku, reqVO.getSku())
                .betweenIfPresent(AdsAutomationPlanDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdsAutomationPlanDO::getId));
    }

}
