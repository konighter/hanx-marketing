package com.hzltd.module.erplus.adv.auto_plan.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationTemplatePageReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationTemplateDO;
import com.hzltd.module.erplus.adv.dal.mysql.automation.AdsAutomationTemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 广告自动化模版 Service
 */
@Service
@Validated
public class AdsAutomationTemplateService {

    @Resource
    private AdsAutomationTemplateMapper adsAutomationTemplateMapper;

    /**
     * 获得广告自动化模版分页
     *
     * @param pageReqVO 分页查询
     * @return 广告自动化模版分页
     */
    public PageResult<AdsAutomationTemplateDO> getTemplatePage(AdsAutomationTemplatePageReqVO pageReqVO) {
        return adsAutomationTemplateMapper.selectPage(pageReqVO);
    }

    /**
     * 获得广告自动化模版
     *
     * @param id 编号
     * @return 广告自动化模版
     */
    public AdsAutomationTemplateDO getTemplate(Long id) {
        return adsAutomationTemplateMapper.selectById(id);
    }

}
