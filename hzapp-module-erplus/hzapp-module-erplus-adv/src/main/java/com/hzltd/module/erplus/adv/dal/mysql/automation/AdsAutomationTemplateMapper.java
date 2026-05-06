package com.hzltd.module.erplus.adv.dal.mysql.automation;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationTemplatePageReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告自动化模版 Mapper
 */
@Mapper
public interface AdsAutomationTemplateMapper extends BaseMapperX<AdsAutomationTemplateDO> {

    default PageResult<AdsAutomationTemplateDO> selectPage(AdsAutomationTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsAutomationTemplateDO>()
                .likeIfPresent(AdsAutomationTemplateDO::getName, reqVO.getName())
                .eqIfPresent(AdsAutomationTemplateDO::getStatus, reqVO.getStatus())
                .orderByDesc(AdsAutomationTemplateDO::getId));
    }

}
