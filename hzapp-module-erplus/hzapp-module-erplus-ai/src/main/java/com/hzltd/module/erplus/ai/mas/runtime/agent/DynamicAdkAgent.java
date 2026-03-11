package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.google.adk.agents.LlmAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.AgentMessage;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GraphMemoryService;
import com.google.adk.runner.Runner;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper class bridging a Google ADK native Agent with our MAS framework.
 * This class translates instructions and context, delegating the actual 
 * model inference and tool calling to the ADK `LlmAgent`.
 * 
 * Refactored to delegate memory management to ADK + GraphMemoryService.
 */
@Slf4j
public class DynamicAdkAgent implements BaseAgent {

    private final String agentRole;
    private final String instruction;
    private final MasEventLogService eventLogService;
    private final Runner runner;
    private static final String APP_NAME = "MAS-Framework";

    public DynamicAdkAgent(String agentRole, 
                           String instruction, 
                           LlmAgent adkAgent, 
                           MasEventLogService eventLogService,
                           GraphMemoryService graphMemoryService) {
        this.agentRole = agentRole;
        this.instruction = instruction;
        this.eventLogService = eventLogService;
        
        // Initialize the runner using the customized GraphMemoryService
        this.runner = Runner.builder()
                .agent(adkAgent)
                .appName(APP_NAME)
                .memoryService(graphMemoryService)
                .build();
    }

    @Override
    public String getInstruction() {
        return instruction;
    }

    @Override
    public String getRoleName() {
        return agentRole;
    }

    @Override
    public void onMessage(AgentMessage message) {
        log.info("[{}] Received message from [{}] (trace: {}): {}", 
                agentRole, message.getSenderRole(), message.getTraceId(), message.getPayload());
    }

    @Override
    public String execute(LoopMemory memory) {
        log.info("[{}] Executing ADK Agent for loop: {}", agentRole, memory.getLoopId());
        
        // 1. Determine the core instruction for this specific execution.
        // We no longer manually append global context (like common_goal) here; 
        // that's now handled by ADK + GraphMemoryService's search capability.
        String dynamicTask = (String) memory.get("taskInstruction");
        String finalPrompt = (dynamicTask != null) ? dynamicTask : instruction;

        try {
            // 2. Delegate to Google ADK Agent via the Runner
            Content content = Content.fromParts(Part.fromText(finalPrompt));
            String sessionId = memory.getSessionId(); 

            // Use sessionId as the ADK userId to ensure that GraphMemoryService searches 
            // the context belonging to this specific MAS run (Macro-Session).
            String adkUserId = sessionId;

            // Ensure session exists in the memory service
            runner.sessionService().createSession(adkUserId, sessionId).blockingGet();

            if (eventLogService != null) {
                eventLogService.logEvent(memory.getSessionId(), memory.getLoopId(), "AGENT_START", agentRole);
            }

            StringBuilder accumulatedResponse = new StringBuilder();
            
            // runAsync will automatically invoke GraphMemoryService.searchMemory
            runner.runAsync(adkUserId, sessionId, content).blockingIterable().forEach(event -> {
                if (eventLogService != null) {
                    eventLogService.logEvent(memory.getSessionId(), memory.getLoopId(), "AGENT_EVENT", event.stringifyContent());
                }
                String snippet = event.stringifyContent();
                if (snippet != null) accumulatedResponse.append(snippet);
            });
            
            String textResult = accumulatedResponse.toString();
            log.debug("[{}] ADK Agent returned execution result (length: {})", agentRole, textResult.length());
            
            if (eventLogService != null) {
                eventLogService.logEvent(memory.getSessionId(), memory.getLoopId(), "AGENT_DONE", agentRole);
            }

            // 3. Persist output back into MAS memory
            memory.put(agentRole + "_output", textResult);
            return textResult;

        } catch (Exception e) {
            log.error("[{}] ADK Agent execution failed for loop {}", agentRole, memory.getLoopId(), e);
            memory.put("error", e.getMessage());
            throw new RuntimeException("Agent execution failed delegating to ADK", e);
        }
    }
}
