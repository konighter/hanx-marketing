package com.hzltd.module.erplus.ai.service;

import com.hzltd.module.erplus.ai.workflow.AdWorkflowConstants;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 广告监控服务，供 Flowable Service Task 调用
 */
@Slf4j
@Service("adMonitorService")
public class AdMonitorService {

    /**
     * 执行监控逻辑
     * @param execution Flowable 执行上下文
     */
    public void monitor(DelegateExecution execution) {
        // 获取启动时传入的业务参数
        String adId = (String) execution.getVariable(AdWorkflowConstants.VAR_AD_ID);
        String tenantId = (String) execution.getVariable(AdWorkflowConstants.VAR_TENANT_ID);

        log.info("[Loop A] 启动广告数据监控... 租户: {}, 广告ID: {}", tenantId, adId);
        
        // 模拟监控逻辑
        // 在实际业务中，这里会调用 AI 接口或大数据分析平台获取实时指标
        boolean isAnomalyDetected = checkDataAnomalies();
        
        if (isAnomalyDetected) {
            log.warn("[Loop A] 检测到广告数据异常，准备触发报警并重启策略流程！");
            
            // 设置流程变量：是否触发报警
            execution.setVariable(AdWorkflowConstants.VAR_ALARM_TRIGGERED, true);
            
            // 封装异常上下文数据，传递给信号及其触发的目标流程
            Map<String, Object> alarmData = new HashMap<>();
            alarmData.put("reason", "ACOS 突增");
            alarmData.put("timestamp", System.currentTimeMillis());
            
            execution.setVariable(AdWorkflowConstants.VAR_ALARM_DATA, alarmData);
        } else {
            log.info("[Loop A] 广告监控数据正常。");
            execution.setVariable(AdWorkflowConstants.VAR_ALARM_TRIGGERED, false);
        }
    }

    private boolean checkDataAnomalies() {
        // 演示逻辑：模拟随机触发报警
        return Math.random() > 0.5;
    }
}
