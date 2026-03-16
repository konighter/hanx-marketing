package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.agents.ParallelAgent;
import com.google.adk.agents.SequentialAgent;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagGenerationPlan;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagPlanNode;
import com.hzltd.module.erplus.ai.mas.runtime.report.MasEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MAS DAG 动态组合器 — 唯一的自研核心组件.
 *
 * <h2>职责</h2>
 * 将 Planner 输出的 {@link DagGenerationPlan}（JSON DAG）动态转换为
 * ADK 可执行的 Agent 图（{@code ParallelAgent} / {@code SequentialAgent}）。
 *
 * <h2>算法：拓扑分层（Kahn's Algorithm）</h2>
 * <pre>
 * 输入 DAG：A → [B, C（并行）] → D
 *
 * 拓扑分层结果：
 *   Tier 1: [A]
 *   Tier 2: [B, C]   ← 同层无依赖，用 ParallelAgent 包装
 *   Tier 3: [D]
 *
 * 构建结果：
 *   SequentialAgent(
 *     LlmAgent(A),
 *     ParallelAgent(LlmAgent(B), LlmAgent(C)),
 *     LlmAgent(D)
 *   )
 * </pre>
 *
 * <h2>session.state key 约定</h2>
 * <p>每个节点通过 {@code outputKey = "{nodeId}_output"} 将结果写入 {@code session.state}。
 * 下游节点通过 LlmAgent instruction 中的 {@code {nodeId}_output} 模板语法自动注入。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MasDagDispatcher {

    // TODO: 注入 MasAgentFactory 以通过 agentRole 构建 LlmAgent
    // private final MasAgentFactory agentFactory;

    /**
     * 将 DagGenerationPlan 编译为 ADK 可执行的 Agent 图.
     *
     * @param plan       Planner 输出的 DAG 计划
     * @param sessionId  Session ID（用于发布事件）
     * @param phaseIndex 当前 Phase 编号（用于节点命名和日志）
     * @param publisher  事件发布器（Callbacks 中调用）
     * @return ADK BaseAgent（SequentialAgent 根节点）
     */
    public BaseAgent compose(DagGenerationPlan plan,
                             String sessionId,
                             int phaseIndex,
                             MasEventPublisher publisher) {
        List<DagPlanNode> nodes = plan.getNodes();
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("DagGenerationPlan has no nodes in phase " + phaseIndex);
        }

        log.info("[MasDagDispatcher] Composing DAG for phase={}, nodes={}", phaseIndex, nodes.size());

        // 1. 拓扑分层
        List<List<DagPlanNode>> tiers = topologicalTiers(nodes);
        log.info("[MasDagDispatcher] Topological tiers: {}", tiers.size());

        // 每一层构建 ADK Agent（LlmAgent extends BaseAgent，需显式转型列表）
        List<BaseAgent> sequence = new ArrayList<>();
        for (int i = 0; i < tiers.size(); i++) {
            List<DagPlanNode> tier = tiers.get(i);
            List<BaseAgent> tierAgents = tier.stream()
                    .map(node -> (BaseAgent) buildAdkAgent(node, sessionId, phaseIndex, publisher))
                    .collect(Collectors.toList());

            if (tierAgents.size() == 1) {
                sequence.add(tierAgents.get(0));
            } else {
                sequence.add(ParallelAgent.builder()
                        .name("Phase-" + phaseIndex + "-Tier-" + i)
                        .subAgents(tierAgents)
                        .build());
            }
        }

        // 3. 组合为 SequentialAgent
        if (sequence.size() == 1) {
            return sequence.get(0);
        }
        return SequentialAgent.builder()
                .name("Phase-" + phaseIndex + "-DAG")
                .subAgents(sequence)
                .build();
    }

    /**
     * 为单个 DAG 节点构建 ADK LlmAgent.
     * <p>
     * <b>outputKey 约定：</b>{@code {nodeId}_output} — 写入 session.state，
     * 供下游 Agent 在 instruction 中通过 {@code {nodeId}_output} 模板语法读取。
     */
    private LlmAgent buildAdkAgent(DagPlanNode node,
                                   String sessionId,
                                   int phaseIndex,
                                   MasEventPublisher publisher) {
        String agentName = node.getId();
        String outputKey = node.getId() + "_output";

        // TODO: 通过 MasAgentFactory.getConfigByRole(node.getAgentRole()) 加载真实配置
        // MasAgentConfigDO config = agentFactory.getConfigByRole(node.getAgentRole());

        LlmAgent agent = LlmAgent.builder()
                .name(agentName)
                // TODO: .model(config.getModelName())
                .model("gemini-2.0-flash")  // 临时默认值
                // TODO: .instruction(buildInstruction(config, node))
                .instruction("TODO: load instruction from config for role=" + node.getAgentRole())
                // 关键：outputKey 确保并发节点写入不同 key，天然无竞争
                .outputKey(outputKey)
                // TODO: .tools(resolveTools(config))
                //
                // --- ADK Callback 注册 ---
                // TODO(adk-callback): 待确认 ADK Java SDK 的 Callback 签名后补充
                //   beforeAgentCallback / afterAgentCallback 的 lambda 签名
                //   取决于 ADK 版本（CallbackContext、Optional<Content> 等）。
                //   当前先省略，通过 MasDagDispatcher 外部机制补偿事件。
                //
                // .beforeAgentCallback(ctx -> {
                //     publisher.publish(MasEvent.builder()...NODE_STARTED...);
                //     return Optional.empty();
                // })
                // .afterAgentCallback(ctx -> {
                //     publisher.publish(MasEvent.builder()...NODE_COMPLETED...);
                //     return Optional.empty();
                // })
                .build();

        log.debug("[MasDagDispatcher] Built LlmAgent: name={}, outputKey={}, role={}",
                  agentName, outputKey, node.getAgentRole());
        return agent;
    }

    /**
     * 对 DAG 节点进行拓扑分层（Kahn's Algorithm）.
     * <p>
     * 同一 Tier 内的节点互相无依赖，可并行执行。
     *
     * @param nodes DAG 节点列表（含 dependsOn 字段）
     * @return 按执行顺序排列的分层列表
     */
    private List<List<DagPlanNode>> topologicalTiers(List<DagPlanNode> nodes) {
        // 建立 id → node 映射
        Map<String, DagPlanNode> nodeMap = new LinkedHashMap<>();
        for (DagPlanNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }

        // 计算每个节点的入度
        Map<String, Integer> inDegree = new HashMap<>();
        for (DagPlanNode node : nodes) {
            inDegree.putIfAbsent(node.getId(), 0);
            if (node.getDependsOn() != null) {
                // 入度 = 直接依赖的节点数量
                inDegree.put(node.getId(), node.getDependsOn().size());
            }
        }

        List<List<DagPlanNode>> tiers = new ArrayList<>();
        while (!inDegree.isEmpty()) {
            // 当前 Tier = 入度为 0 的所有节点
            List<DagPlanNode> currentTier = inDegree.entrySet().stream()
                    .filter(e -> e.getValue() == 0)
                    .map(e -> nodeMap.get(e.getKey()))
                    .filter(Objects::nonNull)
                    .toList();

            if (currentTier.isEmpty()) {
                throw new IllegalStateException("DAG has a cycle or unresolvable dependency");
            }

            tiers.add(currentTier);

            // 移除当前 Tier 节点，减少下游入度
            Set<String> completedIds = new HashSet<>();
            for (DagPlanNode node : currentTier) {
                completedIds.add(node.getId());
                inDegree.remove(node.getId());
            }
            for (DagPlanNode node : nodes) {
                if (inDegree.containsKey(node.getId()) && node.getDependsOn() != null) {
                    long remaining = node.getDependsOn().stream()
                            .filter(dep -> !completedIds.contains(dep))
                            .count();
                    inDegree.put(node.getId(), (int) remaining);
                }
            }
        }

        return tiers;
    }
}
