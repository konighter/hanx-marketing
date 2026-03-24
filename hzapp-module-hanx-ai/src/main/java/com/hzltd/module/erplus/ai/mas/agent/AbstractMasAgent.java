package com.hzltd.module.erplus.ai.mas.agent;

import com.hzltd.module.erplus.ai.mas.communication.AgentMessage;
import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.spi.memory.NodeMemory;
import com.hzltd.module.erplus.ai.mas.spi.llm.MasLlmClient;

import static com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryKeys.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Framework-agnostic agent wrapper that bridges MAS workflow memory with an underlying MasLlmClient.
 */
@Slf4j
public class AbstractMasAgent implements BaseAgent {

    private final String agentRole;
    private final String instruction;
    private final MasEventLogService eventLogService;
    private final MasLlmClient llmClient;

    public AbstractMasAgent(String agentRole, 
                           String instruction, 
                           MasEventLogService eventLogService,
                           MasLlmClient llmClient) {
        this.agentRole = agentRole;
        this.instruction = instruction;
        this.eventLogService = eventLogService;
        this.llmClient = llmClient;
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
    public String execute(NodeMemory memory) {
        log.info("[{}] Executing Agent for node: {}", agentRole, memory.getNodeId());
        
        // 1. Determine the core instruction for this specific execution.
        String dynamicTask = (String) memory.get(TASK_INSTRUCTION);
        String finalPrompt = (dynamicTask != null) ? dynamicTask : instruction;

        try {
            if (eventLogService != null) {
                eventLogService.logEvent(memory.getSessionId(), memory.getNodeId(), "AGENT_START", agentRole);
            }

            // 2. Delegate to generic LLM Client
            String textResult = llmClient.chat(finalPrompt, memory.getSessionId());
            
            if (textResult != null) {
                textResult = textResult.trim();
                if (textResult.startsWith("```json")) textResult = textResult.substring(7);
                else if (textResult.startsWith("```")) textResult = textResult.substring(3);
                if (textResult.endsWith("```")) textResult = textResult.substring(0, textResult.length() - 3);
                textResult = textResult.trim();
            }

            log.debug("[{}] Agent returned execution result (length: {})", agentRole, textResult != null ? textResult.length() : 0);
            
            if (eventLogService != null) {
                eventLogService.logEvent(memory.getSessionId(), memory.getNodeId(), "AGENT_DONE", agentRole);
            }

            // 3. Persist output back into MAS memory
            memory.put(agentOutputKey(memory.getNodeId(), agentRole), textResult);
            return textResult;

        } catch (Exception e) {
            log.error("[{}] Agent execution failed for node {}", agentRole, memory.getNodeId(), e);
            memory.put("error", e.getMessage());
            throw new RuntimeException("Agent execution failed delegating to MasLlmClient", e);
        }
    }
}
