package com.hzltd.module.erplus.adv.service.mas;

import com.hzltd.framework.tenant.core.context.TenantContextHolder;
import com.hzltd.module.erplus.adv.service.MasSkillService;
import com.hzltd.module.erplus.ai.mas.adk.SkillAgentFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用 Skill 任务委托 Bean
 * <p>
 * 供 skill-task-loop 子流程的 ServiceTask 调用。
 * 内部通过 {@link SkillAgentFactory} 路由到对应阶段的 ADK Agent 执行 AI 推理。
 * </p>
 */
@Slf4j
@Service("skillTaskDelegate")
public class SkillTaskDelegate {
    @Resource
    private SkillAgentFactory skillAgentFactory;
    @Resource
    private MasSkillService masSkillService;

    /**
     * 1. 收集数据
     * 调用 CollectAgent，使用查询类 Tools 收集业务数据
     */
    public void collect(DelegateExecution execution) {
        restoreTenantContext(execution);

        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        String skillCode = (String) execution.getVariable("skillCode");
        String targetBizId = (String) execution.getVariable("targetBizId");
        String phaseInstruction = (String) execution.getVariable("phaseInstruction");
        Integer iteration = (Integer) execution.getVariable("iteration");

        log.info("[SkillTask:collect] sessionId={}, phase={}, skill={}, target={}, iteration={}",
                sessionId, phaseName, skillCode, targetBizId, iteration);

        // 构建用户消息
        String userMessage = String.format(
                "请为 ASIN: %s 收集数据。\n当前阶段: %s (第 %d 轮迭代)\n策略指引: %s",
                targetBizId, phaseName, iteration, phaseInstruction
        );

        // 检查报警归因数据
        String alarmAttribution = (String) execution.getVariable("alarmAttribution");
        if (alarmAttribution != null) {
            userMessage += "\n\n⚠️ 报警归因数据: " + alarmAttribution;
        }

        try {
            List<String> tools = getRequiredTools(execution);
            String output = skillAgentFactory.runAgent(skillCode, "collect", sessionId, userMessage, tools);

            execution.setVariable("collectOutput", output);
            logEvent(execution, "INFO", "数据收集完成", truncate(output, 500));

            log.info("[SkillTask:collect] 完成, outputLength={}", output.length());
        } catch (Exception e) {
            log.error("[SkillTask:collect] 执行异常: {}", e.getMessage(), e);
            execution.setVariable("collectOutput", "数据收集异常: " + e.getMessage());
            logEvent(execution, "ERROR", "数据收集失败", e.getMessage());
        }
    }

    /**
     * 2. AI 决策
     * 调用 DecideAgent，产出结构化操作建议
     */
    public void decide(DelegateExecution execution) {
        restoreTenantContext(execution);

        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        String phaseInstruction = (String) execution.getVariable("phaseInstruction");
        String skillCode = (String) execution.getVariable("skillCode");
        Integer iteration = (Integer) execution.getVariable("iteration");

        log.info("[SkillTask:decide] sessionId={}, phase={}, iteration={}", sessionId, phaseName, iteration);

        String userMessage = String.format(
                "基于已收集的数据，请制定操作方案。\n当前阶段: %s (第 %d 轮迭代)\n策略规则: %s",
                phaseName, iteration, phaseInstruction
        );

        try {
            List<String> tools = getRequiredTools(execution);
            String output = skillAgentFactory.runAgent(skillCode, "decide", sessionId, userMessage, tools);

            execution.setVariable("decisionOutput", output);
            // 默认需要人工确认（后续可根据 AI 输出动态判断风险等级）
            execution.setVariable("requiresConfirm", true);
            logEvent(execution, "SUCCESS", "策略制定完成", truncate(output, 500));

            log.info("[SkillTask:decide] 完成, outputLength={}", output.length());
        } catch (Exception e) {
            log.error("[SkillTask:decide] AI 决策异常: {}", e.getMessage(), e);
            execution.setVariable("decisionOutput", "决策异常: " + e.getMessage());
            execution.setVariable("requiresConfirm", true);
            logEvent(execution, "ERROR", "策略制定失败", e.getMessage());
        }
    }

    /**
     * 3. 执行策略
     * 调用 ExecuteAgent，逐条执行操作指令
     */
    public void execute(DelegateExecution execution) {
        restoreTenantContext(execution);

        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        String skillCode = (String) execution.getVariable("skillCode");
        String decisionOutput = (String) execution.getVariable("decisionOutput");

        log.info("[SkillTask:execute] sessionId={}, phase={}, 开始执行策略...", sessionId, phaseName);

        String userMessage = "请严格按照以下操作方案逐条执行:\n\n" + decisionOutput;

        try {
            List<String> tools = getRequiredTools(execution);
            String output = skillAgentFactory.runAgent(skillCode, "execute", sessionId, userMessage, tools);

            execution.setVariable("executionResult", output);
            logEvent(execution, "SUCCESS", "策略执行完成", truncate(output, 500));

            log.info("[SkillTask:execute] 完成, outputLength={}", output.length());
        } catch (Exception e) {
            log.error("[SkillTask:execute] 执行异常: {}", e.getMessage(), e);
            execution.setVariable("executionResult", "执行异常: " + e.getMessage());
            logEvent(execution, "ERROR", "策略执行失败", e.getMessage());
        }
    }

    /**
     * 4. 复盘分析
     * 调用 ReviewAgent，对比执行前后的数据变化
     */
    public void review(DelegateExecution execution) {
        restoreTenantContext(execution);

        String sessionId = (String) execution.getVariable("sessionId");
        String phaseName = (String) execution.getVariable("phaseName");
        String skillCode = (String) execution.getVariable("skillCode");
        String targetBizId = (String) execution.getVariable("targetBizId");
        Integer iteration = (Integer) execution.getVariable("iteration");

        log.info("[SkillTask:review] sessionId={}, phase={}, iteration={}, 开始复盘分析...",
                sessionId, phaseName, iteration);

        String userMessage = String.format(
                "请对 ASIN: %s 进行复盘分析。\n当前阶段: %s (第 %d 轮迭代)\n请查询最新数据，对比执行前后的变化，评估策略效果。",
                targetBizId, phaseName, iteration
        );

        try {
            List<String> tools = getRequiredTools(execution);
            String output = skillAgentFactory.runAgent(skillCode, "review", sessionId, userMessage, tools);

            execution.setVariable("reviewResult", output);
            logEvent(execution, "INFO", "复盘分析完成", truncate(output, 500));

            log.info("[SkillTask:review] 完成, outputLength={}", output.length());
        } catch (Exception e) {
            log.error("[SkillTask:review] 复盘异常: {}", e.getMessage(), e);
            execution.setVariable("reviewResult", "复盘异常: " + e.getMessage());
            logEvent(execution, "ERROR", "复盘分析失败", e.getMessage());
        }
    }

    // ========== 私有方法 ==========

    /**
     * 从流程变量恢复租户上下文
     */
    private void restoreTenantContext(DelegateExecution execution) {
        Object tenantIdObj = execution.getVariable("tenantId");
        if (tenantIdObj != null) {
            Long tenantId = Long.valueOf(tenantIdObj.toString());
            TenantContextHolder.setTenantId(tenantId);
            log.debug("[SkillTask] Restored tenant context: {}", tenantId);
        }
    }

    /**
     * 从流程变量获取 Skill 声明的 requiredTools
     */
    private List<String> getRequiredTools(DelegateExecution execution) {
        String toolsJson = (String) execution.getVariable("phaseTools");
        return SkillAgentFactory.parseRequiredTools(toolsJson);
    }

    /**
     * 记录技能实例日志
     */
    private void logEvent(DelegateExecution execution, String type, String title, String content) {
        try {
            // 获取实例 ID (通过 businessKey 反查或直接从变量获取)
            Object instanceIdObj = execution.getVariable("instanceId");
            if (instanceIdObj != null) {
                Long instanceId = Long.valueOf(instanceIdObj.toString());
                masSkillService.logSkillInstanceEvent(instanceId, type, title, content);
            }
        } catch (Exception e) {
            log.warn("[SkillTask] Failed to log event: {}", e.getMessage());
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
