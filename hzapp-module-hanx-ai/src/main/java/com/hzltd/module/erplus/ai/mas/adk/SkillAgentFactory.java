package com.hzltd.module.erplus.ai.mas.adk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.adk.agents.LlmAgent;
import com.google.adk.agents.RunConfig;
import com.google.adk.runner.Runner;
import com.google.adk.tools.BaseTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.ai.mas.llm.LlmProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill Agent 工厂。
 * <p>
 * 职责:
 * 1. 按 skillCode 构建并缓存 4 个阶段的 LlmAgent (collect / decide / execute / review)
 * 2. 提供按 phase 路由的 Agent 获取能力
 * 3. 管理共享的 Runner (同一 Skill 实例的 4 个阶段共享 Session)
 * </p>
 */
@Slf4j
@Service
public class SkillAgentFactory {

    private static final String APP_NAME = "MAS-Skill";

    private final MasToolRegistry toolRegistry;

    /**
     * 缓存 key: skillCode, value: 4 个阶段的 Agent Map
     */
    private final ConcurrentHashMap<String, Map<String, AgentWithRunner>> agentCache = new ConcurrentHashMap<>();

    public SkillAgentFactory(MasToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    /**
     * 执行指定阶段的 Agent，返回最终文本输出
     *
     * @param skillCode     技能编码
     * @param phase         阶段名: collect / decide / execute / review
     * @param sessionId     会话 ID (tenantId:skillCode:targetBizId)
     * @param userMessage   用户消息 (含阶段指引)
     * @param requiredTools Skill 声明的 Tool 列表
     * @return Agent 的文本输出
     */
    public String runAgent(String skillCode, String phase, String sessionId,
                           String userMessage, List<String> requiredTools) {
        AgentWithRunner awr = getAgentWithRunner(skillCode, phase, requiredTools);

        log.info("[SkillAgentFactory] Running agent: skill={}, phase={}, sessionId={}",
                skillCode, phase, sessionId);

        Content content = Content.fromParts(Part.fromText(userMessage));
        RunConfig runConfig = RunConfig.builder().setAutoCreateSession(true).build();

        StringBuilder output = new StringBuilder();

        // 使用 blockingIterable 同步执行
        awr.runner.runAsync(sessionId, sessionId, content, runConfig)
                .blockingIterable()
                .forEach(event -> {
                    String snippet = event.stringifyContent();
                    if (snippet != null) {
                        output.append(snippet);
                    }
                });

        log.info("[SkillAgentFactory] Agent execution completed: skill={}, phase={}, outputLength={}",
                skillCode, phase, output.length());

        return output.toString();
    }

    /**
     * 获取指定 Skill 指定阶段的 AgentWithRunner
     */
    private AgentWithRunner getAgentWithRunner(String skillCode, String phase, List<String> requiredTools) {
        Map<String, AgentWithRunner> agentMap = agentCache.computeIfAbsent(skillCode,
                code -> buildAgentTree(code, requiredTools));
        AgentWithRunner awr = agentMap.get(phase);
        if (awr == null) {
            throw new IllegalArgumentException("Unknown phase: " + phase + " for skill: " + skillCode);
        }
        return awr;
    }

    /**
     * 构建 4 个阶段的 Agent Tree，每个 Agent 配一个 Runner
     */
    private Map<String, AgentWithRunner> buildAgentTree(String skillCode, List<String> requiredTools) {
        log.info("[SkillAgentFactory] Building agent tree for skill: {}, tools: {}", skillCode, requiredTools);

        List<String> tools = requiredTools != null ? requiredTools : Collections.emptyList();

        return Map.of(
                "collect", buildPhaseAgentWithRunner(skillCode, "collect", tools, COLLECT_INSTRUCTION),
                "decide", buildPhaseAgentWithRunner(skillCode, "decide", tools, DECIDE_INSTRUCTION),
                "execute", buildPhaseAgentWithRunner(skillCode, "execute", tools, EXECUTE_INSTRUCTION),
                "review", buildPhaseAgentWithRunner(skillCode, "review", tools, REVIEW_INSTRUCTION)
        );
    }

    private AgentWithRunner buildPhaseAgentWithRunner(String skillCode, String phase,
                                                       List<String> requiredTools, String instruction) {
        List<BaseTool> phaseTools = toolRegistry.getToolsForPhase(requiredTools, phase);

        String agentName = skillCode + "_" + phase;
        LlmAgent agent = LlmAgent.builder()
                .name(agentName)
                .model(LlmProvider.defaultLlm())
                .instruction(instruction)
                .tools(phaseTools)
                .build();

        Runner runner = Runner.builder()
                .agent(agent)
                .appName(APP_NAME)
                .build();

        return new AgentWithRunner(agent, runner);
    }

    /**
     * 解析 Skill 的 requiredTools JSON 字段
     */
    public static List<String> parseRequiredTools(String requiredToolsJson) {
        if (requiredToolsJson == null || requiredToolsJson.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return JsonUtils.parseObject(requiredToolsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Agent 和 Runner 的组合
     */
    private record AgentWithRunner(LlmAgent agent, Runner runner) {}

    // ========== Agent Instruction 模板 ==========

    private static final String COLLECT_INSTRUCTION = """
            你是一个广告数据分析助手。你的任务是收集和整理目标商品的相关业务数据。
            
            请根据策略指引，调用可用的数据查询工具收集必要信息。收集完成后，请输出一份结构化的数据报告，包含:
            1. 广告活动概览（活动数量、总花费、总点击、总订单）
            2. 核心指标汇总（ACOS、CTR、CVR、ROAS）
            3. 关键词表现摘要（高效词、低效词、无转化词）
            4. 竞品状况（如适用）
            5. 库存和 Listing 状态
            
            请确保数据完整、准确，为后续的策略制定提供充分的信息基础。
            """;

    private static final String DECIDE_INSTRUCTION = """
            你是一个广告策略专家。基于已收集的数据，按照策略规则制定具体的操作方案。
            
            请输出结构化的操作建议，使用 JSON 数组格式，每条建议包含:
            - actionType: 操作类型 (adjustBid / addKeyword / removeKeyword / toggleCampaignStatus / adjustPlacementBid / adjustKeywordBid / adjustPrice / updateListing)
            - target: 操作目标ID（campaignId / keywordId / asin）
            - params: 操作参数（如 newBid, matchType 等）
            - reason: 执行理由
            - expectedEffect: 预期效果
            
            注意:
            ⚠️ 你不能直接执行任何操作，只能输出建议方案。
            ⚠️ 每条建议必须有充分的数据支撑和理由。
            ⚠️ 请按优先级排序，高 ROI 影响的操作排在前面。
            """;

    private static final String EXECUTE_INSTRUCTION = """
            你是一个广告操作执行器。你的任务是严格按照操作方案逐条执行。
            
            规则:
            ⚠️ 不要自行发挥或修改方案中的参数。
            ⚠️ 严格按照方案中每一条指令的 actionType、target、params 执行。
            ⚠️ 执行后记录每条操作的结果（成功/失败/原因）。
            ⚠️ 如果某条操作失败，继续执行下一条，不要中断。
            
            执行完成后，输出执行结果汇总报告。
            """;

    private static final String REVIEW_INSTRUCTION = """
            你是一个广告策略复盘分析师。你的任务是评估策略执行的效果。
            
            请通过查询最新数据，与执行前的数据进行对比分析:
            1. 核心指标变化（ACOS、CTR、CVR、ROAS 的对比）
            2. 操作效果评估（每条执行操作的效果）
            3. ROI 评估（投入产出分析）
            4. 优化建议（下一轮迭代应关注的重点）
            
            请输出结构化的复盘报告，为下一轮决策提供参考。
            """;
}
