package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.google.adk.tools.BaseTool;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.spi.llm.MasLlmClient;
import com.hzltd.module.erplus.ai.mas.runtime.spi.llm.adk.AdkLlmClientAdapter;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.adk.AdkMemoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Factory class that converts the database persistent DO (`MasAgentConfigDO`) 
 * into a functional `DynamicMasAgent` configured via SPI implementations.
 */
@Slf4j
@Component
public class MasAgentFactory {

    private final UnifiedToolRegistry toolRegistry;
    private final MasEventLogService eventLogService;
    private final MasMemoryManager memoryManager;


    public MasAgentFactory(UnifiedToolRegistry toolRegistry, 
                           MasEventLogService eventLogService,
                           MasMemoryManager memoryManager) {
        this.toolRegistry = toolRegistry;
        this.eventLogService = eventLogService;
        this.memoryManager = memoryManager;
    }

    /**
     * Builds a DynamicMasAgent based on the DB configuration.
     */
    public DynamicMasAgent createFromConfig(MasAgentConfigDO configDO) {
        log.info("Building MAS agent for role: {}", configDO.getRoleCode());

        // 1. Initialize the internal ADK model backend
        BaseLlm llm = initializeLanguageModel(configDO.getExtConfig());

        // 2. Bind the tools to this agent
        List<BaseTool> tools = resolveTools(configDO.getToolBeans());

        // 3. Build the core ADK Agent
        LlmAgent adkNativeAgent = LlmAgent.builder()
                .name(configDO.getAgentName())
                .model(llm)
                .instruction(configDO.getSystemPrompt())
                .tools(tools) 
                .build();
                
        // Wrap using our LLM SPI. 
        // We create an AdkMemoryAdapter on the fly using our agnostic memoryManager.
        MasLlmClient llmClient = new AdkLlmClientAdapter(adkNativeAgent, new AdkMemoryAdapter(memoryManager));

        // 4. Wrap it in our framework structure
        return new DynamicMasAgent(configDO.getRoleCode(), configDO.getSystemPrompt(), eventLogService, llmClient);
    }

    private BaseLlm initializeLanguageModel(String extConfigJson) {
        String modelName = "gemini-1.5-pro";

        if (extConfigJson != null && !extConfigJson.trim().isEmpty()) {
            try {
                Map<String, Object> configMap = JsonUtils.parseObject(extConfigJson, new TypeReference<>() {});
                if (configMap.containsKey("modelName")) {
                    modelName = configMap.get("modelName").toString();
                }
            } catch (Exception e) {
                log.warn("Failed to parse extConfig JSON: {}. Using default model configurations.", extConfigJson);
            }
        }
        
        return Gemini.builder()
                .modelName(modelName)
                .apiKey("MOCK_OR_ENV_API_KEY") 
                .build();
    }

    private List<BaseTool> resolveTools(String toolBeansJson) {
        if (toolBeansJson == null || toolBeansJson.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<String> beanNames = JsonUtils.parseObject(toolBeansJson, new TypeReference<>() {});
            return toolRegistry.resolveTools(beanNames);
        } catch (Exception e) {
            log.error("Failed to parse toolBeans JSON: {}", toolBeansJson, e);
            return Collections.emptyList();
        }
    }
}
