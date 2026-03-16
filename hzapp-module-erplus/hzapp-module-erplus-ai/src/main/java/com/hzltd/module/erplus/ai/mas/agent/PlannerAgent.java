package com.hzltd.module.erplus.ai.mas.agent;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.spi.memory.NodeMemory;
import com.hzltd.module.erplus.ai.mas.prompt.PromptTemplateFactory;
import com.hzltd.module.erplus.ai.mas.prompt.schema.DagGenerationPlan;
import com.hzltd.module.erplus.ai.mas.spi.llm.MasLlmClient;
import com.hzltd.module.erplus.ai.mas.spi.llm.adk.AdkLlmClientAdapter;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.adk.AdkMemoryAdapter;
import static com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryKeys.*;
import com.hzltd.module.erplus.ai.mas.tools.ToolDefinition;
import com.hzltd.module.erplus.ai.mas.tools.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * System Agent that acts as the primary macro-planner for Workflow Orchestration.
 * Generates DagGenerationPlans dynamically based on goal, history, available agents and tools.
 */
@Slf4j
@Component
public class PlannerAgent extends AbstractMasAgent {

    public static final String ROLE_CODE = "PLANNER";

    private final PromptTemplateFactory promptTemplateFactory;

    private final ToolRegistry toolRegistry;
    private final CustomAgentLoaderService agentLoaderService;

    // =====================================================================
    // System Prompt Template — structured for precise task decomposition
    // =====================================================================
    private static final String PLANNER_PROMPT_TEMPLATE =
        "# Role\n" +
        "You are a Workflow Planner Agent. Your job is to decompose a complex goal into executable phases.\n" +
        "Each phase is a small DAG (directed acyclic graph) of agent tasks that can run in parallel or in sequence.\n\n" +
        // ... (rest of prompt remains same)
        "# Goal\n" +
        "${userGoal}\n\n" +
        "# Current Phase\n" +
        "Phase ${phaseCount}\n\n" +
        "# Execution History\n" +
        "${globalContext}\n\n" +
        "# Available Agents\n" +
        "${availableAgents}\n\n" +
        "# Available Tools (for REACT nodes)\n" +
        "${availableTools}\n\n" +
        "# Planning Rules\n" +
        "1. **Single Responsibility**: Each node should do ONE clear thing. Do NOT put multiple distinct tasks in one node.\n" +
        "2. **Minimum Viable Phase**: Each phase should contain 1-5 nodes. Prefer smaller phases — it's better to have more phases than large complex DAGs.\n" +
        "3. **Dependency Clarity**: Use `dependsOn` to express sequential requirements. Nodes without `dependsOn` run in parallel.\n" +
        "4. **Node Type Selection**:\n" +
        "   - `SIMPLE`: Pure reasoning/writing/analysis that needs NO external data. Uses a single LLM call.\n" +
        "   - `REACT`: Tasks that need to interact with external systems (DB queries, API calls, file operations). Uses a Think-Act-Observe loop with tools.\n" +
        "5. **Tool Scoping**: For REACT nodes, specify only the tools that node needs in `toolSet`. This improves focus and safety.\n" +
        "6. **Read History Carefully**: Before planning, review the execution history to understand what has been done, what failed, and what remains.\n" +
        "7. **Self-Healing**: If a previous phase had failures, plan recovery actions in this phase (e.g., retry with different parameters, reduce scope, or skip).\n" +
        "7. **SUSPEND**: If the task requires waiting (for data to accumulate, for external events, for user approval), set status=SUSPEND with `nextExecuteAt` in ISO format.\n" +
        "8. **Completion**: Return status=DONE only when ALL sub-goals are fully achieved. Do NOT return DONE prematurely.\n" +
        "9. **Instruction Quality**: Each node's `instruction` must be specific and actionable. Include:\n" +
        "    - What data to use (reference previous outputs by agent role name)\n" +
        "    - What the expected output should contain\n" +
        "    - Any constraints, thresholds, or formatting requirements\n\n" +
        "# Output Format\n" +
        "Output ONLY a pure JSON object (NO markdown fences, NO explanation before or after the JSON):\n" +
        "{\n" +
        "  \"status\": \"IN_PROGRESS | DONE | FAILED | SUSPEND\",\n" +
        "  \"reasoning\": \"Why you chose this plan (1-2 sentences)\",\n" +
        "  \"nextExecuteAt\": \"2026-03-13T10:00:00 (only when status=SUSPEND)\",\n" +
        "  \"nodes\": [\n" +
        "    {\n" +
        "      \"id\": \"p${phaseCount}-n1\",\n" +
        "      \"agentRole\": \"ANALYST\",\n" +
        "      \"instruction\": \"Specific, actionable instruction referencing previous outputs if needed\",\n" +
        "      \"dependsOn\": [],\n" +
        "      \"nodeType\": \"REACT\",\n" +
        "      \"toolSet\": [\"database_query\"]\n" +
        "    }\n" +
        "  ]\n" +
        "}\n" +
        "IMPORTANT: Use node IDs like p${phaseCount}-n1, p${phaseCount}-n2, etc. to keep them unique across phases.\n";

    @Autowired
    public PlannerAgent(MasEventLogService eventLogService,
                        PromptTemplateFactory promptTemplateFactory,
                        MasMemoryManager memoryManager,
                        @Autowired(required = false) ToolRegistry toolRegistry,
                        CustomAgentLoaderService agentLoaderService) {
        this(buildMasLlmClient(memoryManager), eventLogService, promptTemplateFactory, toolRegistry, agentLoaderService);
    }

    public PlannerAgent(MasLlmClient llmClient,
                        MasEventLogService eventLogService,
                        PromptTemplateFactory promptTemplateFactory,
                        ToolRegistry toolRegistry,
                        CustomAgentLoaderService agentLoaderService) {
        super(ROLE_CODE, "Analyze and plan macro-workflow stages.", eventLogService, llmClient);
        this.promptTemplateFactory = promptTemplateFactory;
        this.toolRegistry = toolRegistry;
        this.agentLoaderService = agentLoaderService;
    }

    private static MasLlmClient buildMasLlmClient(MasMemoryManager memoryManager) {
        BaseLlm fallbackLlm = Gemini.builder()
                .modelName("gemini-1.5-pro")
                .apiKey("MOCK_DEFAULT_KEY")
                .build();

        LlmAgent adkAgent = LlmAgent.builder()
                .name("Workflow Planner")
                .model(fallbackLlm)
                .instruction("You are a strict JSON-speaking workflow coordinator.")
                .build();
                
        // Wrap generic memory manager into ADK adapter specifically for this client
        return new AdkLlmClientAdapter(adkAgent, new AdkMemoryAdapter(memoryManager));
    }

    @Override
    public String execute(NodeMemory memory) {
        // ... (rest of execute remains same)
        log.info("[PlannerAgent] Starting planning phase.");

        String userGoal = memory.get(USER_GOAL) != null ? memory.get(USER_GOAL).toString() : "Unknown goal";
        String globalContext = memory.get(GLOBAL_HISTORY) != null ? memory.get(GLOBAL_HISTORY).toString() : "No prior history — this is Phase 1.";
        String phaseCount = memory.get(PHASE_COUNT) != null ? memory.get(PHASE_COUNT).toString() : "1";

        String availableAgents = buildAvailableAgentsList();
        String availableTools = buildAvailableToolsList();

        String dynamicPrompt = promptTemplateFactory.render(PLANNER_PROMPT_TEMPLATE, Map.of(
            "userGoal", userGoal,
            "globalContext", globalContext,
            "phaseCount", phaseCount,
            "availableAgents", availableAgents,
            "availableTools", availableTools
        ));

        memory.put(TASK_INSTRUCTION, dynamicPrompt);
        log.info("[PlannerAgent] Delegating task to sub-agent chain...");
        
        String result = super.execute(memory);

        try {
            JsonUtils.parseObject(result, DagGenerationPlan.class);
        } catch (Exception e) {
            log.warn("[PlannerAgent] Model output failed JSON validation. Raw: {}", result, e);
            throw new RuntimeException("PlannerAgent returned invalid JSON format", e);
        }

        return result;
    }

    private String buildAvailableAgentsList() {
        try {
            Set<String> roles = agentLoaderService.getAvailableRoles();
            if (roles == null || roles.isEmpty()) {
                return "No custom agents configured. Use generic roles: ANALYST, EXECUTOR, STRATEGIST, WRITER.";
            }
            return roles.stream()
                    .filter(r -> !r.equals(ROLE_CODE))
                    .map(r -> "- " + r)
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.warn("[PlannerAgent] Failed to enumerate agents", e);
            return "Agent enumeration unavailable. Use generic roles: ANALYST, EXECUTOR, STRATEGIST, WRITER.";
        }
    }

    private String buildAvailableToolsList() {
        if (toolRegistry == null) return "No tools registered. Use SIMPLE nodes only.";
        try {
            List<ToolDefinition> defs = toolRegistry.getToolDefinitions();
            if (defs.isEmpty()) return "No tools registered. Use SIMPLE nodes only.";
            return defs.stream()
                    .map(ToolDefinition::toPromptDescription)
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.warn("[PlannerAgent] Failed to enumerate tools", e);
            return "Tool enumeration unavailable.";
        }
    }
}
