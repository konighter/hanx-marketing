package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.google.adk.tools.BaseTool;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Factory class that converts the database persistent DO (`MasAgentConfigDO`) 
 * into a functional `DynamicAdkAgent` wrapping the Google ADK core `Agent`.
 */
@Slf4j
@Component
public class AdkAgentFactory {

    private final UnifiedToolRegistry toolRegistry;
    private final MasEventLogService eventLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdkAgentFactory(UnifiedToolRegistry toolRegistry, MasEventLogService eventLogService) {
        this.toolRegistry = toolRegistry;
        this.eventLogService = eventLogService;
    }

    /**
     * Builds a DynamicAdkAgent based on the DB configuration.
     */
    public DynamicAdkAgent createFromConfig(MasAgentConfigDO configDO) {
        log.info("Building ADK agent for role: {}", configDO.getRoleCode());

        // 1. Initialize the internal ADK model backend
        BaseLlm llm = initializeLanguageModel(configDO.getExtConfig());

        // 2. Bind the tools to this agent (via Spring Application Context)
        List<BaseTool> tools = resolveTools(configDO.getToolBeans());

        // 3. Build the core ADK Agent
        LlmAgent adkNativeAgent = LlmAgent.builder()
                .name(configDO.getAgentName())
                .model(llm)
                .instruction(configDO.getSystemPrompt())
                .tools(tools) 
                .build();

        // 4. Wrap it in our framework structure
        return new DynamicAdkAgent(configDO.getRoleCode(), configDO.getSystemPrompt(), adkNativeAgent, eventLogService);
    }

    private BaseLlm initializeLanguageModel(String extConfigJson) {
        // Default configs
        String modelName = "gemini-1.5-pro";

        if (extConfigJson != null && !extConfigJson.trim().isEmpty()) {
            try {
                Map<String, Object> configMap = objectMapper.readValue(extConfigJson, new TypeReference<>() {});
                if (configMap.containsKey("temperature")) {
                    // temperature = Double.parseDouble(configMap.get("temperature").toString());
                    // Note: ADK 0.8.0 Gemini builder doesn't expose temperature directly.
                }
                if (configMap.containsKey("modelName")) {
                    modelName = configMap.get("modelName").toString();
                }
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse extConfig JSON: {}. Using default model configurations.", extConfigJson);
            }
        }
        
        // Use ADK 0.8.0 native Gemini client
        // Provide placeholder API key, should ideally come from env or property
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
            List<String> beanNames = objectMapper.readValue(toolBeansJson, new TypeReference<List<String>>() {});
            return toolRegistry.resolveTools(beanNames);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse toolBeans JSON: {}", toolBeansJson, e);
            return Collections.emptyList();
        }
    }
}

