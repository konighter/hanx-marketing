package com.hzltd.module.erplus.adv.service.mas;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * 报警处理委托 Bean
 * 供 skill-seq-tasks 主流程的报警处理 Event Sub-Process 调用
 * 执行归因分析，将结论注入主流程变量空间，供后续 Phase 循环读取
 */
@Slf4j
@Service("alarmDelegate")
public class AlarmDelegate {

    /**
     * 归因分析
     * 分析报警数据，确定异常原因，给出建议动作
     * 结果写入主流程变量空间（Event Sub-Process 共享父流程上下文）
     *
     * @param execution Flowable 执行上下文
     */
    public void analyze(DelegateExecution execution) {
        String skillCode = (String) execution.getVariable("skillCode");
        String targetBizId = (String) execution.getVariable("targetBizId");
        String alarmData = (String) execution.getVariable("alarmData");

        log.info("[AlarmHandler] 开始归因分析, skillCode={}, targetBizId={}", skillCode, targetBizId);
        log.info("[AlarmHandler] 报警数据: {}", alarmData);

        // TODO: 实现归因分析逻辑
        // 1. 解析 alarmData 获取异常指标
        // 2. 调用 AI 或规则引擎进行归因
        // 3. 确定建议动作类型
        String attribution = performAttribution(skillCode, alarmData);
        String suggestedAction = decideSuggestedAction(attribution);

        // 写入主流程变量空间，后续 Phase 循环可以读取这些变量做决策调整
        execution.setVariable("alarmAttribution", attribution);
        execution.setVariable("suggestedAction", suggestedAction);

        log.info("[AlarmHandler] 归因完成, attribution={}, suggestedAction={}", attribution, suggestedAction);
    }

    /**
     * 执行暂停动作
     * 当异常严重时，暂停相关投放
     */
    public void executePause(DelegateExecution execution) {
        String targetBizId = (String) execution.getVariable("targetBizId");
        log.warn("[AlarmHandler:PAUSE] 暂停投放, targetBizId={}", targetBizId);

        // TODO: 调用广告 API 暂停投放
        // 例: advService.pauseCampaign(targetBizId);
    }

    /**
     * 执行调整动作
     * 当异常可控时，自动微调参数
     */
    public void executeAdjust(DelegateExecution execution) {
        String targetBizId = (String) execution.getVariable("targetBizId");
        String attribution = (String) execution.getVariable("alarmAttribution");
        log.info("[AlarmHandler:ADJUST] 调整参数, targetBizId={}, 基于归因: {}", targetBizId, attribution);

        // TODO: 根据归因结果自动微调
        // 例: advService.adjustBid(targetBizId, adjustmentPlan);
    }

    /**
     * 仅通知
     * 当异常轻微时，仅记录和通知，不做实际干预
     */
    public void executeNotify(DelegateExecution execution) {
        String targetBizId = (String) execution.getVariable("targetBizId");
        String attribution = (String) execution.getVariable("alarmAttribution");
        log.info("[AlarmHandler:NOTIFY] 发送通知, targetBizId={}, attribution={}", targetBizId, attribution);

        // TODO: 发送通知（站内信、邮件、企业微信等）
    }

    /**
     * 执行归因分析
     */
    private String performAttribution(String skillCode, String alarmData) {
        // TODO: 接入 AI 或规则引擎做归因
        // 示例归因结果: { "rootCause": "竞争加剧", "affectedMetric": "ACOS", "severity": "MEDIUM" }
        return String.format("{\"rootCause\":\"待分析\",\"skillCode\":\"%s\",\"timestamp\":%d}",
                skillCode, System.currentTimeMillis());
    }

    /**
     * 根据归因结果决定建议动作
     *
     * @return PAUSE / ADJUST / NOTIFY_ONLY
     */
    private String decideSuggestedAction(String attribution) {
        // TODO: 根据归因的 severity 和 rootCause 判断
        // HIGH severity → PAUSE
        // MEDIUM severity → ADJUST
        // LOW severity → NOTIFY_ONLY
        return "NOTIFY_ONLY"; // 默认仅通知
    }
}
