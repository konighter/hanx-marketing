package com.hzltd.module.erplus.adv.service.mas;

import com.hzltd.module.erplus.ai.mas.orchestration.MasOrchestrationResult;
import com.hzltd.module.erplus.ai.mas.orchestration.WorkflowOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * 通用 Skill 任务委托 Bean
 * 供 skill-task-loop 子流程的 ServiceTask 调用
 * 内部通过 WorkflowOrchestrator 执行 AI 推理
 */
@Slf4j
@Service("skillTaskDelegate")
public class SkillTaskDelegate {

    private final WorkflowOrchestrator workflowOrchestrator;

    public SkillTaskDelegate(WorkflowOrchestrator workflowOrchestrator) {
        this.workflowOrchestrator = workflowOrchestrator;
    }

    /**
     * 收集数据
     * 读取业务数据、历史投放数据，写入会话内存供后续决策使用
     */
    public void collect(DelegateExecution execution) {
        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        String skillCode = (String) execution.getVariable("skillCode");
        String targetBizId = (String) execution.getVariable("targetBizId");
        Integer iteration = (Integer) execution.getVariable("iteration");

        log.info("[SkillTask:collect] sessionId={}, phase={}, skill={}, target={}, iteration={}",
                sessionId, phaseName, skillCode, targetBizId, iteration);

        // 检查是否有报警归因数据需要纳入本轮决策
        String alarmAttribution = (String) execution.getVariable("alarmAttribution");
        if (alarmAttribution != null) {
            log.info("[SkillTask:collect] 发现报警归因数据, 将纳入本轮决策上下文");
        }

        // TODO: 实现业务数据收集逻辑
        // 1. 根据 skillCode 和 targetBizId 获取业务数据
        // 2. 写入 AI 会话内存 (通过 MasMemoryManager)
    }

    /**
     * AI 决策
     * 调用 WorkflowOrchestrator 执行 AI 推理循环，产出策略建议
     */
    public void decide(DelegateExecution execution) {
        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        String instruction = (String) execution.getVariable("phaseInstruction");
        Integer iteration = (Integer) execution.getVariable("iteration");

        log.info("[SkillTask:decide] sessionId={}, phase={}, iteration={}", sessionId, phaseName, iteration);

        // 构造目标描述（指导书 + 当前迭代信息）
        String goal = String.format("[Phase: %s, Iteration: %d] %s", phaseName, iteration, instruction);

        try {
            WorkflowOrchestrator orchestrator = workflowOrchestrator;
            MasOrchestrationResult result = orchestrator.executeMacroLoop(sessionId, goal);

            log.info("[SkillTask:decide] AI 决策完成, resultType={}", result.getType());

            // 将结果写入流程变量
            execution.setVariable("decisionOutput", result.getOutput());
            execution.setVariable("decisionHistory", result.getHistory());

            // 默认需要人工确认（实际可根据 AI 输出动态判断风险等级）
            execution.setVariable("requiresConfirm", true);

        } catch (Exception e) {
            log.error("[SkillTask:decide] AI 决策异常: {}", e.getMessage(), e);
            execution.setVariable("decisionOutput", "决策异常: " + e.getMessage());
            execution.setVariable("requiresConfirm", true); // 异常时强制人工确认
        }
    }

    /**
     * 执行策略
     * 根据决策结果执行具体的操作（如广告竞价调整、预算修改等）
     */
    public void execute(DelegateExecution execution) {
        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        String decisionOutput = (String) execution.getVariable("decisionOutput");

        log.info("[SkillTask:execute] sessionId={}, phase={}, 开始执行策略...", sessionId, phaseName);

        // TODO: 实现策略执行逻辑
        // 1. 解析 decisionOutput 中的具体操作指令
        // 2. 调用业务 API 执行操作
        // 3. 记录执行结果

        execution.setVariable("executionResult", "策略已执行");
        log.info("[SkillTask:execute] 策略执行完成");
    }

    /**
     * 复盘分析
     * 对比执行前后的数据变化，评估策略效果
     */
    public void review(DelegateExecution execution) {
        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        Integer iteration = (Integer) execution.getVariable("iteration");

        log.info("[SkillTask:review] sessionId={}, phase={}, iteration={}, 开始复盘分析...",
                sessionId, phaseName, iteration);

        // TODO: 实现复盘分析逻辑
        // 1. 获取执行前后的核心指标对比
        // 2. 评估策略效果（ROI、ACOS 等）
        // 3. 将分析结果写入会话内存，供下一次 collect/decide 使用

        execution.setVariable("reviewResult", "复盘完成，效果待评估");
        log.info("[SkillTask:review] 复盘分析完成");
    }
}
