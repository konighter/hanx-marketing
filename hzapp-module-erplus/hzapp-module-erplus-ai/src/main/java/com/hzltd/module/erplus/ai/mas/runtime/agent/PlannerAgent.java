package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.PromptTemplateFactory;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagGenerationPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * System Agent that acts as the primary macro-planner for Workflow Orchestration.
 * Replaces or upgrades the older ProjectManagerAgent pattern. Focuses on dynamically outputting DagGenerationPlans.
 */
@Slf4j
@Component
public class PlannerAgent extends DynamicAdkAgent {

    public static final String ROLE_CODE = "PLANNER";
    
    private final PromptTemplateFactory promptTemplateFactory;
    private final ObjectMapper objectMapper;
    
    private static final String PLANNER_PROMPT_TEMPLATE = 
        "You are the Workflow Planner Agent. Your overarching goal is: ${userGoal}\n" +
        "Current history and state to base your decision on: ${globalContext}\n" +
        "Please analyze the situation and generate the next stage of execution to move closer to the goal. " +
        "You MUST output ONLY a pure JSON object that strictly adheres to the following structure, with NO markdown formatting around it (no ```json): \n" +
        "{\n" +
        "  \"status\": \"IN_PROGRESS or DONE or FAILED\",\n" +
        "  \"reasoning\": \"Explain why you chose these nodes or this status\",\n" +
        "  \"nodes\": [\n" +
        "    { \"id\": \"node_uuid\", \"agentRole\": \"CODER_OR_OTHER\", \"instruction\": \"Specific instruction\", \"dependsOn\": [\"other_node_uuid\"] }\n" +
        "  ]\n" +
        "}\n";

    @Autowired
    public PlannerAgent(MasEventLogService eventLogService, 
                        PromptTemplateFactory promptTemplateFactory, 
                        ObjectMapper objectMapper,
                        com.hzltd.module.erplus.ai.mas.runtime.memory.GraphMemoryService graphMemoryService) {
        this(buildNativeAgent(), eventLogService, promptTemplateFactory, objectMapper, graphMemoryService);
    }

    public PlannerAgent(LlmAgent adkNativeAgent, 
                        MasEventLogService eventLogService, 
                        PromptTemplateFactory promptTemplateFactory, 
                        ObjectMapper objectMapper,
                        com.hzltd.module.erplus.ai.mas.runtime.memory.GraphMemoryService graphMemoryService) {
        super(ROLE_CODE, "Analyze and plan macro-workflow stages.", adkNativeAgent, eventLogService, graphMemoryService);
        this.promptTemplateFactory = promptTemplateFactory;
        this.objectMapper = objectMapper;
        log.info("Initialized system planner agent: {}", ROLE_CODE);
    }

    private static LlmAgent buildNativeAgent() {
        // Fallback model config using Gemini
        BaseLlm fallbackLlm = Gemini.builder()
                .modelName("gemini-1.5-pro")
                .apiKey("MOCK_DEFAULT_KEY") 
                .build();

        return LlmAgent.builder()
                .name("Workflow Planner")
                .model(fallbackLlm)
                .instruction("You are a strict JSON-speaking workflow coordinator.")
                .build();
    }
    
    @Override
    public String execute(LoopMemory memory) {
        log.info("[PlannerAgent] Starting planning phase.");
        
        String userGoal = memory.get("userGoal") != null ? memory.get("userGoal").toString() : "Unknown goal";
        String globalContext = memory.get("globalHistory") != null ? memory.get("globalHistory").toString() : "No prior history.";
        
        String dynamicPrompt = promptTemplateFactory.render(PLANNER_PROMPT_TEMPLATE, Map.of(
            "userGoal", userGoal,
            "globalContext", globalContext
        ));
        
        // Pass the highly customized prompt into memory for the DynamicAdkAgent to use
        memory.put("taskInstruction", dynamicPrompt);
        
        // Delegate to base dynamic execution (calls ADK LlmAgent)
        String result = super.execute(memory);
        
        // Post-processing to ensure JSON parsing works smoothly
        if (result != null) {
            result = result.trim();
            if (result.startsWith("```json")) {
                result = result.substring(7);
            } else if (result.startsWith("```")) {
                result = result.substring(3);
            }
            if (result.endsWith("```")) {
                result = result.substring(0, result.length() - 3);
            }
            result = result.trim();
        }
        
        try {
            // Validate it parses correctly to ensure deterministic outputs 
            DagGenerationPlan plan = objectMapper.readValue(result, DagGenerationPlan.class);
            log.info("[PlannerAgent] Successfully generated valid JSON DagGenerationPlan with macro status: {}", plan.getStatus());
        } catch (Exception e) {
            log.warn("[PlannerAgent] Model output failed JSON validation. Raw: {}", result, e);
            throw new RuntimeException("PlannerAgent returned invalid JSON format that couldn't be mapped to DagGenerationPlan", e);
        }
        
        return result;
    }
}
