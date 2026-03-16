package com.hzltd.module.erplus.ai.mas.agent;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.prompt.schema.StrategicPlan;
import com.hzltd.module.erplus.ai.mas.spi.memory.NodeMemory;
import com.hzltd.module.erplus.ai.mas.spi.llm.MasLlmClient;
import com.hzltd.module.erplus.ai.mas.spi.llm.adk.AdkLlmClientAdapter;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.adk.AdkMemoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryKeys.TASK_INSTRUCTION;

/**
 * System Agent that acts as a default orchestrator and project manager.
 * Supports Strategic Planning: Splitting a UserGoal into discrete MasTasks.
 */
@Slf4j
@Component
public class ProjectManagerAgent extends AbstractMasAgent {

    public static final String ROLE_CODE = "PM";



    private static final String STRATEGIC_PLAN_PROMPT_TEMPLATE =
            "# Role\n" +
            "You are the Strategic Project Manager for a Multi-Agent System (MAS).\n" +
            "Your goal is to decompose a high-level User Goal into discrete, manageable Business Tasks.\n\n" +
            "# Strategic Rules\n" +
            "1. **Task Types**: \n" +
            "   - **LEAF**: A practical execution unit. This is the only type that will be sent to the WorkflowOrchestrator.\n" +
            "   - **SEQUENTIAL**: A container for tasks that must run one after another.\n" +
            "   - **PARALLEL**: A container for tasks that can run simultaneously.\n" +
            "2. **Granularity**: Do not over-split. Aim for 3-7 major milestones for a complex goal.\n" +
            "3. **Human Gating**: If a step requires external data that might be missing or user approval, mark it as a separate stage (e.g. for SEQUENTIAL/LEAF flow).\n" +
            "4. **Output Context**: Each task's `goal` description must be self-contained enough for a downstream agent to understand its specific boundary.\n\n" +
            "# Input Goal\n" +
            "${userGoal}\n\n" +
            "# Output Format\n" +
            "Output ONLY a pure JSON object (NO markdown fences, NO explanation):\n" +
            "{\n" +
            "  \"reasoning\": \"Strategic explanation for this decomposition\",\n" +
            "  \"parentTaskType\": \"SEQUENTIAL or PARALLEL\",\n" +
            "  \"tasks\": [\n" +
            "    {\n" +
            "      \"name\": \"Task Name\",\n" +
            "      \"type\": \"LEAF or SEQUENTIAL or PARALLEL\",\n" +
            "      \"goal\": \"Specific business milestone to achieve\",\n" +
            "      \"priority\": 1,\n" +
            "      \"executionOrder\": 1,\n" +
            "      \"dependencies\": [\"DependencyTaskName\"]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    @Autowired
    public ProjectManagerAgent(MasEventLogService eventLogService, 
                               MasMemoryManager memoryManager) {
        this(buildMasLlmClient(memoryManager), eventLogService);
    }

    public ProjectManagerAgent(MasLlmClient llmClient, MasEventLogService eventLogService) {
        super(ROLE_CODE, "Coordinate multi-agent collaboration and strategic planning.", eventLogService, llmClient);
    }

    private static MasLlmClient buildMasLlmClient(MasMemoryManager memoryManager) {
        BaseLlm fallbackLlm = Gemini.builder()
                .modelName("gemini-1.5-pro")
                .apiKey("MOCK_DEFAULT_KEY")
                .build();

        LlmAgent adkAgent = LlmAgent.builder()
                .name("Project Manager")
                .model(fallbackLlm)
                .instruction("You are a helpful project manager coordinating AI agents. Always output strictly valid JSON.")
                .build();
                
        return new AdkLlmClientAdapter(adkAgent, new AdkMemoryAdapter(memoryManager));
    }

    /**
     * Performs strategic decomposition of a goal into a StrategicPlan.
     */
    public StrategicPlan planStrategicTasks(String userGoal, NodeMemory memory) {
        log.info("[ProjectManagerAgent] Starting strategic planning for goal: {}", userGoal);
        
        String prompt = STRATEGIC_PLAN_PROMPT_TEMPLATE.replace("${userGoal}", userGoal);
        memory.put(TASK_INSTRUCTION, prompt);
        
        String result = super.execute(memory);
        


        try {
            return JsonUtils.parseObject(result, StrategicPlan.class);
        } catch (Exception e) {
            log.error("[ProjectManagerAgent] Failed to parse StrategicPlan JSON. Raw: {}", result, e);
            throw new RuntimeException("ProjectManagerAgent returned invalid strategic plan format", e);
        }
    }

    @Override
    public String execute(NodeMemory memory) {
        log.info("[ProjectManagerAgent] Starting project coordination.");
        return super.execute(memory);
    }
}
