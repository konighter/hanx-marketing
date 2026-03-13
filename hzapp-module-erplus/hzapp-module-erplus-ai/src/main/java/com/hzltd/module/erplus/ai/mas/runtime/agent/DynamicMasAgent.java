package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.spi.llm.MasLlmClient;

/**
 * A runtime agent instantiated dynamically from database configuration.
 */
public class DynamicMasAgent extends AbstractMasAgent {

    public DynamicMasAgent(String agentRole, 
                           String instruction, 
                           MasEventLogService eventLogService, 
                           MasLlmClient llmClient) {
        super(agentRole, instruction, eventLogService, llmClient);
    }
}
