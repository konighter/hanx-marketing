package com.hzltd.module.erplus.ai.mas.agent;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.spi.memory.NodeMemory;
import com.hzltd.module.erplus.ai.mas.orchestration.MasOrchestrationResult;
import com.hzltd.module.erplus.ai.mas.prompt.PromptTemplateFactory;
import com.hzltd.module.erplus.ai.mas.spi.llm.MasLlmClient;
import com.hzltd.module.erplus.ai.mas.spi.llm.adk.AdkLlmClientAdapter;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.adk.AdkMemoryAdapter;
import static com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryKeys.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Reviewer Agent responsible for analyzing execution failures, fixing outputs,
 * and providing final verdicts when loop limits are reached.
 */
@Slf4j
@Component
public class ReviewerAgent extends AbstractMasAgent {

    public static final String ROLE_CODE = "REVIEWER";
    
    private final PromptTemplateFactory promptTemplateFactory;

    
    private static final String REVIEW_PROMPT_TEMPLATE = 
        "You are the Workflow Reviewer Agent. The system encountered an issue or reached its execution limit.\n" +
        "User Goal: ${userGoal}\n" +
        "Current history and state: ${globalContext}\n" +
        "Issue encountered: ${issue}\n\n" +
        "Please analyze the situation and provide a final verdict. " +
        "You MUST output ONLY a pure JSON object that strictly adheres to the following structure: \n" +
        "{\n" +
        "  \"type\": \"FINISH or FAIL or SUSPEND\",\n" +
        "  \"reasoning\": \"Explain your decision\",\n" +
        "  \"errorMessage\": \"Details of the failure if applicable\",\n" +
        "  \"historySummary\": \"A concise summary of what was accomplished and what remains\"\n" +
        "}\n";

    @Autowired
    public ReviewerAgent(MasEventLogService eventLogService, 
                        PromptTemplateFactory promptTemplateFactory, 
                        MasMemoryManager memoryManager) {
        this(eventLogService, promptTemplateFactory, buildMasLlmClient(memoryManager));
    }

    public ReviewerAgent(MasEventLogService eventLogService, 
                        PromptTemplateFactory promptTemplateFactory, 
                        MasLlmClient llmClient) {
        super(ROLE_CODE, "Review and finalize workflow status.", eventLogService, llmClient);
        this.promptTemplateFactory = promptTemplateFactory;
    }

    private static MasLlmClient buildMasLlmClient(MasMemoryManager memoryManager) {
        BaseLlm fallbackLlm = Gemini.builder()
                .modelName("gemini-1.5-pro")
                .apiKey("MOCK_DEFAULT_KEY") 
                .build();

        LlmAgent adkAgent = LlmAgent.builder()
                .name("Workflow Reviewer")
                .model(fallbackLlm)
                .instruction("You are a strict JSON-speaking workflow reviewer and debugger.")
                .build();
                
        return new AdkLlmClientAdapter(adkAgent, new AdkMemoryAdapter(memoryManager));
    }
    
    public MasOrchestrationResult review(NodeMemory memory, String issue) {
        log.info("[ReviewerAgent] Starting review for issue: {}", issue);
        
        String userGoal = memory.get(USER_GOAL) != null ? memory.get(USER_GOAL).toString() : "Unknown goal";
        String globalContext = memory.get(GLOBAL_HISTORY) != null ? memory.get(GLOBAL_HISTORY).toString() : "No prior history.";
        
        String dynamicPrompt = promptTemplateFactory.render(REVIEW_PROMPT_TEMPLATE, Map.of(
            "userGoal", userGoal,
            "globalContext", globalContext,
            "issue", issue
        ));
        
        memory.put(TASK_INSTRUCTION, dynamicPrompt);
        String result = super.execute(memory);
        


        try {
            Map<String, Object> map = JsonUtils.parseObject(result, Map.class);
            String typeStr = (String) map.get("type");
            MasOrchestrationResult.ResultType type = MasOrchestrationResult.ResultType.FAIL;
            if ("FINISH".equals(typeStr)) type = MasOrchestrationResult.ResultType.FINISH;
            else if ("SUSPEND".equals(typeStr)) type = MasOrchestrationResult.ResultType.SUSPEND;
            else if ("CONTINUE".equals(typeStr)) type = MasOrchestrationResult.ResultType.CONTINUE;

            return MasOrchestrationResult.builder()
                    .type(type)
                    .errorMessage((String) map.get("errorMessage"))
                    .history((String) map.get("historySummary"))
                    .build();
        } catch (Exception e) {
            log.error("[ReviewerAgent] Failed to parse reviewer output: {}", result, e);
            return MasOrchestrationResult.builder()
                    .type(MasOrchestrationResult.ResultType.FAIL)
                    .errorMessage("Reviewer agent output was unparseable: " + result)
                    .build();
        }
    }
}
