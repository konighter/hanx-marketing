package com.hzltd.module.erplus.ai.service;

import com.hzltd.module.erplus.ai.workflow.AdWorkflowConstants;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * 广告策略执行服务，供 Flowable Service Task 调用
 */
@Slf4j
@Service("adStrategyService")
public class AdStrategyService {

    /**
     * 收集广告数据
     */
    public void collect(DelegateExecution execution) {
        String adId = (String) execution.getVariable(AdWorkflowConstants.VAR_AD_ID);
        log.info("[Loop B] ASIN: {} - 正在准备策略执行，收集历史投放与实时消费数据...", adId);
    }

    /**
     * 产出策略决策
     */
    public String decide(DelegateExecution execution) {
        String adId = (String) execution.getVariable(AdWorkflowConstants.VAR_AD_ID);
        log.info("[Loop B] ASIN: {} - 调用 AI Agent 产出最优广告优化决策...", adId);
        
        // 模拟逻辑：如果是大额调整，则需要人工确认
        boolean needsConfirm = true; // 实际场景可根据 AI 决策结果动态设置
        execution.setVariable(AdWorkflowConstants.VAR_REQUIRES_CONFIRM, needsConfirm);
        
        // 返回决策 ID 或描述
        return "DECISION_DECREASE_BID_20_PERCENT";
    }

    /**
     * 执行动作
     * @param execution Flowable 执行上下文
     */
    public void execute(DelegateExecution execution) {
        String adId = (String) execution.getVariable(AdWorkflowConstants.VAR_AD_ID);
        log.info("[Loop B] ASIN: {} - 广告策略动作已开始执行...", adId);
    }

    /**
     * 复盘分析
     */
    public void review(DelegateExecution execution) {
        String adId = (String) execution.getVariable(AdWorkflowConstants.VAR_AD_ID);
        log.info("[Loop B] ASIN: {} - 正在针对上一次执行的动作进行转化复盘与 ROI 分析...", adId);
    }
}
