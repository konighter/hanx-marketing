package com.hzltd.module.erplus.adv.service.mas;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Skill 监控委托 Bean
 * 供 skill-seq-tasks 主流程的监控 Event Sub-Process 调用
 * 根据 skillCode 委托给对应的指标检查器
 */
@Slf4j
@Service("skillMonitorDelegate")
public class SkillMonitorDelegate {

    /**
     * 执行监控检查
     * 根据 skillCode 确定需要检查的指标集，检测是否异常
     *
     * @param execution Flowable 执行上下文
     */
    public void monitor(DelegateExecution execution) {
        String skillCode = (String) execution.getVariable("skillCode");
        String targetBizId = (String) execution.getVariable("targetBizId");

        log.info("[SkillMonitor] 执行监控检查, skillCode={}, targetBizId={}", skillCode, targetBizId);

        try {
            // 根据 skillCode 获取对应的监控指标配置
            boolean anomalyDetected = checkMetrics(skillCode, targetBizId);

            execution.setVariable("alarmTriggered", anomalyDetected);

            if (anomalyDetected) {
                String alarmContext = buildAlarmContext(skillCode, targetBizId);
                execution.setVariable("alarmData", alarmContext);
                log.warn("[SkillMonitor] 检测到异常! skillCode={}, targetBizId={}, alarmData={}",
                        skillCode, targetBizId, alarmContext);
            } else {
                log.info("[SkillMonitor] 监控指标正常");
            }
        } catch (Exception e) {
            log.error("[SkillMonitor] 监控执行异常: {}", e.getMessage(), e);
            // 监控失败不应影响主流程，不触发报警
            execution.setVariable("alarmTriggered", false);
        }
    }

    /**
     * 根据 skillCode 检查对应的核心指标
     * TODO: 实现具体的指标检查器注册表 (MetricCheckerRegistry)
     */
    private boolean checkMetrics(String skillCode, String targetBizId) {
        // TODO: 根据 skillCode 从注册表中获取对应的指标检查器
        // 示例:
        //   - NEW_PRODUCT_AD_SP_STRATEGY → 检查 ACOS, CTR, Impressions
        //   - BRAND_DEFENSE_STRATEGY     → 检查 Impression Share, Top-of-Search %
        //   - KEYWORD_HARVEST_STRATEGY   → 检查 新关键词转化率, Search Term 覆盖度
        log.debug("[SkillMonitor] 检查指标, skillCode={}, targetBizId={}", skillCode, targetBizId);
        return false; // 默认无异常
    }

    /**
     * 构建报警上下文数据
     */
    private String buildAlarmContext(String skillCode, String targetBizId) {
        // TODO: 实现具体的报警上下文构建，包含异常指标名、当前值、阈值、趋势等
        return String.format("{\"skillCode\":\"%s\",\"targetBizId\":\"%s\",\"timestamp\":%d,\"reason\":\"指标异常\"}",
                skillCode, targetBizId, System.currentTimeMillis());
    }
}
