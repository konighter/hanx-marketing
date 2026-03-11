package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.google.adk.agents.LlmAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.AgentMessage;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import com.google.adk.agents.RunConfig;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.adk.agents.InvocationContext;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper class bridging a Google ADK native Agent with our MAS framework.
 * This class translates instructions and context, delegating the actual 
 * model inference and tool calling to the ADK `LlmAgent`.
 */
@Slf4j
public class DynamicAdkAgent implements BaseAgent {

    private final String roleName;
    private final String instruction;
    private final LlmAgent adkNativeAgent;
    private final MasEventLogService eventLogService;

    public DynamicAdkAgent(String roleName, String instruction, LlmAgent adkNativeAgent, MasEventLogService eventLogService) {
        this.roleName = roleName;
        this.instruction = instruction;
        this.adkNativeAgent = adkNativeAgent;
        this.eventLogService = eventLogService;
    }

    @Override
    public String getInstruction() {
        return instruction;
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public void onMessage(AgentMessage message) {
        log.info("[{}] Received message from [{}] (trace: {}): {}", 
                roleName, message.getSenderRole(), message.getTraceId(), message.getPayload());
        // Custom asynchronous A2A processing logic could be added here
    }

    @Override
    public String execute(LoopMemory memory) {
        log.info("[{}] Executing ADK Agent for loop: {}", roleName, memory.getLoopId());
        
        // 1. Prepare contextual instruction by extracting globals or previous loop results
        StringBuilder contextualPrompt = new StringBuilder();
        contextualPrompt.append("Task Instruction:\n").append(instruction).append("\n\n");
        
        // Optional: Expose loop memory keys to the prompt if explicitly required
        if (memory.get("common_goal") != null) {
            contextualPrompt.append("Global Goal Context: ").append(memory.get("common_goal")).append("\n\n");
        }
        
        // This supports the Coordinator/Dispatcher pattern by allowing a parent agent 
        // to pass specific runtime instructions.
        if (memory.get("taskInstruction") != null) {
            contextualPrompt.append("Specific Assigned Task: ").append(memory.get("taskInstruction")).append("\n\n");
        }

        try {
            // 2. Delegate to Google ADK Agent using InvocationContext
            // For now, we stub out external services and create a simple RunConfig context
            RunConfig config = RunConfig.builder().build();
            Content content = Content.builder()
                    .role("user")
                    .parts(List.of(
                            Part.builder()
                                    .text(contextualPrompt.toString())
                                    .build()
                    ))
                    .build();
                    
            InvocationContext invContext = InvocationContext.builder()
                    .invocationId("session-" + memory.getLoopId())
                    .agent(adkNativeAgent)
                    .userContent(content)
                    .runConfig(config)
                    .build();
            
            if (eventLogService != null) {
                eventLogService.logEvent(memory.getSessionId(), memory.getLoopId(), "AGENT_START", roleName);
            }

            // ADK 0.8.0 runAsync returns RxJava flowable of events. 
            // We subscribe to capture granular events (like tool calls) before blocking for completion.
            StringBuilder finalContent = new StringBuilder();
            adkNativeAgent.runAsync(invContext).blockingIterable().forEach(event -> {
                if (eventLogService != null) {
                    // Log specific event types if needed (e.g. ToolInvocation)
                    String eventTrigger = event.getClass().getSimpleName();
                    eventLogService.logEvent(memory.getSessionId(), memory.getLoopId(), "AGENT_EVENT:" + eventTrigger, event.stringifyContent());
                }
                // Typically the content is accumulated or captured in the last event
                String snippet = event.stringifyContent();
                if (snippet != null) finalContent.append(snippet);
            });
            
            String textResult = finalContent.toString();
            
            log.debug("[{}] ADK Agent returned execution result", roleName);
            
            if (eventLogService != null) {
                eventLogService.logEvent(memory.getSessionId(), memory.getLoopId(), "AGENT_DONE", roleName);
            }

            // 3. Persist output into memory for downstream nodes
            memory.put(roleName + "_output", textResult);
            
            return textResult;

        } catch (Exception e) {
            log.error("[{}] ADK Agent execution failed for loop {}", roleName, memory.getLoopId(), e);
            memory.put("error", e.getMessage());
            throw new RuntimeException("Agent execution failed wrapping ADK", e);
        }
    }
}
