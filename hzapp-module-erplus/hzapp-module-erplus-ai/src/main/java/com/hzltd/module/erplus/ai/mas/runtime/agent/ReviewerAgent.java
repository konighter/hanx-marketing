package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.PromptTemplateFactory;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.MasOrchestrationResult;
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
public class ReviewerAgent extends DynamicAdkAgent {

    public static final String ROLE_CODE = "REVIEWER";
    
    private final PromptTemplateFactory promptTemplateFactory;
    private final ObjectMapper objectMapper;
    
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
                        ObjectMapper objectMapper,
                        com.hzltd.module.erplus.ai.mas.runtime.memory.GraphMemoryService graphMemoryService) {
        super(ROLE_CODE, "Review and finalize workflow status.", buildNativeAgent(), eventLogService, graphMemoryService);
        this.promptTemplateFactory = promptTemplateFactory;
        this.objectMapper = objectMapper;
    }

    private static LlmAgent buildNativeAgent() {
        BaseLlm fallbackLlm = Gemini.builder()
                .modelName("gemini-1.5-pro")
                .apiKey("MOCK_DEFAULT_KEY") 
                .build();

        return LlmAgent.builder()
                .name("Workflow Reviewer")
                .model(fallbackLlm)
                .instruction("You are a strict JSON-speaking workflow reviewer and debugger.")
                .build();
    }
    
    public MasOrchestrationResult review(LoopMemory memory, String issue) {
        log.info("[ReviewerAgent] Starting review for issue: {}", issue);
        
        String userGoal = memory.get("userGoal") != null ? memory.get("userGoal").toString() : "Unknown goal";
        String globalContext = memory.get("globalHistory") != null ? memory.get("globalHistory").toString() : "No prior history.";
        
        String dynamicPrompt = promptTemplateFactory.render(REVIEW_PROMPT_TEMPLATE, Map.of(
            "userGoal", userGoal,
            "globalContext", globalContext,
            "issue", issue
        ));
        
        memory.put("taskInstruction", dynamicPrompt);
        String result = super.execute(memory);
        
        // Basic cleaning
        if (result != null) {
            result = result.trim();
            if (result.startsWith("```json")) result = result.substring(7);
            else if (result.startsWith("```")) result = result.substring(3);
            if (result.endsWith("```")) result = result.substring(0, result.length() - 3);
            result = result.trim();
        }

        try {
            Map<String, Object> map = objectMapper.readValue(result, Map.class);
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
