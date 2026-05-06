package com.hzltd.module.erplus.adv.automation.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.automation.controller.admin.vo.AdsAutomationLogPageReqVO;
import com.hzltd.module.erplus.adv.automation.domain.AdsAction;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationLogDO;
import com.hzltd.module.erplus.adv.dal.mysql.automation.AdsAutomationLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 广告自动化日志 Service
 */
@Service
public class AdsAutomationLogService {

    @Resource
    private AdsAutomationLogMapper adsAutomationLogMapper;

    /**
     * 记录自动化操作日志
     *
     * @param planId       计划 ID
     * @param ruleName     触发规则名称
     * @param triggerData  触发指标
     * @param action       执行动作
     */
    public void recordLog(Long planId, String ruleName, Map<String, Object> triggerData, AdsAction action) {
        AdsAutomationLogDO logDO = AdsAutomationLogDO.builder()
                .planId(planId)
                .ruleName(ruleName)
                .triggerData(triggerData)
                .actionTaken(action != null ? action.toString() : "UNKNOWN")
                .createTime(LocalDateTime.now())
                .build();
        adsAutomationLogMapper.insert(logDO);
    }

    /**
     * 获得广告自动化日志分页
     *
     * @param pageReqVO 分页查询
     * @return 广告自动化日志分页
     */
    public PageResult<AdsAutomationLogDO> getLogPage(AdsAutomationLogPageReqVO pageReqVO) {
        return adsAutomationLogMapper.selectPage(pageReqVO);
    }

}
