package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default System Agent that acts as the primary orchestrator/planner for the MAS group.
 * While DB-loaded agents are preferred, this hardcoded agent ensures the system always has a manager.
 */
@Slf4j
@Component
public class ProjectManagerAgent extends DynamicAdkAgent {

    public static final String ROLE_CODE = "PROJECT_MANAGER";
    
    private static final String DEFAULT_PROMPT = 
        "You are the Project Manager Agent. Your role is to break down the user's request, " +
        "create a plan, and assign tasks to other specialized agents. You supervise the overall execution " +
        "and synthesize the final result.";

    @Autowired
    public ProjectManagerAgent(MasEventLogService eventLogService, 
                               com.hzltd.module.erplus.ai.mas.runtime.memory.GraphMemoryService graphMemoryService) {
        super(ROLE_CODE, "Analyze the request and create a multi-agent plan.", buildNativeAgent(), eventLogService, graphMemoryService);
        log.info("Initialized default system agent: {}", ROLE_CODE);
    }

    private static LlmAgent buildNativeAgent() {
        // Fallback model config using Gemini
        BaseLlm fallbackLlm = Gemini.builder()
                .modelName("gemini-1.5-pro")
                .apiKey("MOCK_DEFAULT_KEY") // Assumes environment has appropriate credentials if actual call is made
                .build();

        return LlmAgent.builder()
                .name("Default Project Manager")
                .model(fallbackLlm)
                .instruction(DEFAULT_PROMPT)
                .build();
    }
    
    @Override
    public String execute(LoopMemory memory) {
        log.info("[ProjectManagerAgent] Starting execution for instruction: {}", getInstruction());
        
        // Custom pre-processing for the manager, e.g., analyzing the full global state
        String globalContext = memory.get("ALL_REPORTS") != null 
                ? memory.get("ALL_REPORTS").toString() 
                : "No prior reports.";
                 
        log.debug("Manager reviewed global context: {}", globalContext);
        
        // Delegate to base dynamic execution
        return super.execute(memory);
    }
}
