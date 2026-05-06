package com.hzltd.module.erplus.adv.dal.mysql.automation;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationLogPageReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告自动化日志 Mapper
 */
@Mapper
public interface AdsAutomationLogMapper extends BaseMapperX<AdsAutomationLogDO> {

    default PageResult<AdsAutomationLogDO> selectPage(AdsAutomationLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsAutomationLogDO>()
                .eqIfPresent(AdsAutomationLogDO::getPlanId, reqVO.getPlanId())
                .orderByDesc(AdsAutomationLogDO::getId));
    }

}
