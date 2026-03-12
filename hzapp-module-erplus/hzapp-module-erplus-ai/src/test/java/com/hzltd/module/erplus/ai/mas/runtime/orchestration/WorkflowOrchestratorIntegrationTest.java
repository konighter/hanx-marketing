package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.erplus.ai.mas.runtime.agent.PlannerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.agent.DynamicAdkAgent;
import com.hzltd.module.erplus.ai.mas.runtime.agent.CustomAgentLoaderService;
import com.hzltd.module.erplus.ai.mas.runtime.agent.AdkAgentFactory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSessionVariableMapper;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.communication.A2AMessageBus;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasEventLogMapper;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.PromptTemplateFactory;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import com.google.adk.agents.LlmAgent;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.UUID;
import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Flowable;
import com.google.adk.models.LlmResponse;
import com.google.adk.models.LlmRequest;
import com.google.adk.models.BaseLlmConnection;

/**
 * End-to-end integration test connecting explicitly to Nvidia's API endpoint.
 * This tests the real reasoning capability of the PlannerAgent.
 */
public class WorkflowOrchestratorIntegrationTest {

    private WorkflowOrchestrator orchestrator;
    private MasMemoryService memoryService;
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Custom BaseLlm implementation to target Nvidia API directly because ADK's Gemini
     * implementation is too opinionated about the baseUrl path.
     */
    public static class NvidiaLlm extends com.google.adk.models.BaseLlm {
        private final String apiKey;
        private final String modelName;

        public NvidiaLlm(String modelName, String apiKey) {
            super(modelName);
            this.modelName = modelName;
            this.apiKey = apiKey;
        }

        @Override
        public io.reactivex.rxjava3.core.Flowable<com.google.adk.models.LlmResponse> generateContent(
                com.google.adk.models.LlmRequest request, boolean stream) {
            
            return io.reactivex.rxjava3.core.Flowable.fromCallable(() -> {
                
                // Extract prompt from request parts
                String prompt = "";
                if (request.contents() != null) {
                    for (Content c : request.contents()) {
                        List<Part> parts = c.parts().orElse(Collections.emptyList());
                        for (Part p : parts) {
                            if (p.text().isPresent()) {
                                prompt += p.text().get() + "\n";
                            }
                        }
                    }
                }
                prompt = prompt.trim();
                System.out.println("[NvidiaLlm] Calling API with prompt: " + (prompt.length() > 500 ? prompt.substring(0, 500) + "..." : prompt));

                String requestBody = "{\"model\": \"" + modelName + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + 
                        prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]}";

                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create("https://integrate.api.nvidia.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .timeout(java.time.Duration.ofSeconds(300)) // Increased timeout
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                java.net.http.HttpResponse<String> response = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
                String respBody = response.body();
                
                // Proper JSON extraction using local ObjectMapper
                String result = "";
                try {
                    com.fasterxml.jackson.databind.ObjectMapper localMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode root = localMapper.readTree(respBody);
                    result = root.path("choices").get(0).path("message").path("content").asText();
                } catch (Exception e) {
                    System.out.println("[NvidiaLlm] JSON parsing failed: " + e.getMessage());
                    result = respBody; // Fallback
                }

                System.out.println("[NvidiaLlm] Received response: " + (result.length() > 500 ? result.substring(0, 500) + "..." : result));
                
                // Wrap in LlmResponse using content builder
                Content content = Content.builder()
                        .parts(List.of(Part.builder().text(result).build()))
                        .build();

                return com.google.adk.models.LlmResponse.builder()
                        .content(content)
                        .build();
            });
        }

        @Override
        public com.google.adk.models.BaseLlmConnection connect(com.google.adk.models.LlmRequest request) {
            throw new UnsupportedOperationException("Not implemented for test");
        }
    }

    @BeforeEach
    public void setup() {
        // 1. Setup real infrastructure but mock the MAPPER (Persistence layer)
        MasEventLogMapper mockEventMapper = Mockito.mock(MasEventLogMapper.class);
        MasEventLogService eventLogService = new MasEventLogService(mockEventMapper);
        MasCheckpointService checkpointService = Mockito.mock(MasCheckpointService.class);
        A2AMessageBus messageBus = new A2AMessageBus();
        PromptTemplateFactory promptFactory = new PromptTemplateFactory();
        
        MasSessionVariableMapper mockVarMapper = Mockito.mock(MasSessionVariableMapper.class);
        when(mockVarMapper.selectList(Mockito.any())).thenReturn(new java.util.ArrayList<>());
        memoryService = new MasMemoryService(mockVarMapper);
        com.hzltd.module.erplus.ai.mas.runtime.memory.GraphMemoryService graphMemoryService = 
                new com.hzltd.module.erplus.ai.mas.runtime.memory.GraphMemoryService(memoryService);

        // 2. Wrap Nvidia API in ADK BaseLlm
        NvidiaLlm liveModel = new NvidiaLlm(
            "qwen/qwen3-next-80b-a3b-instruct", 
            "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"
        );

        LlmAgent nativePlannerAgent = LlmAgent.builder()
                .name("Real Qwen Planner")
                .model(liveModel)
                .instruction("You are a strict JSON-speaking workflow coordinator. ONLY output raw JSON.")
                .build();

        // 3. Setup real PlannerAgent using the new testable constructor
        PlannerAgent plannerAgent = new PlannerAgent(nativePlannerAgent, eventLogService, promptFactory, objectMapper, graphMemoryService, null, null);

        // 4. Setup real Loader stack with mocked Mapper
        com.hzltd.module.erplus.ai.dal.mysql.mas.MasAgentConfigMapper mockConfigMapper = Mockito.mock(com.hzltd.module.erplus.ai.dal.mysql.mas.MasAgentConfigMapper.class);
        
        // Mock a CODER, RESEARCHER, DESIGNER etc. to be loaded as real DynamicAdkAgents
        // We reuse the same liveModel for them in this integration test for simplicity
        AdkAgentFactory agentFactory = new AdkAgentFactory(new com.hzltd.module.erplus.ai.mas.runtime.agent.UnifiedToolRegistry(Mockito.mock(org.springframework.context.ApplicationContext.class)), eventLogService, graphMemoryService) {
            @Override
            public DynamicAdkAgent createFromConfig(com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO configDO) {
                LlmAgent nativeAgent = LlmAgent.builder()
                        .name(configDO.getAgentName())
                        .model(liveModel)
                        .instruction(configDO.getSystemPrompt())
                        .build();
                return new DynamicAdkAgent(configDO.getRoleCode(), configDO.getSystemPrompt(), nativeAgent, eventLogService, graphMemoryService);
            }
        };

        CustomAgentLoaderService loaderService = new CustomAgentLoaderService(mockConfigMapper, agentFactory);
        
        // Mock DB entries for the roles we expect the planner to use
        java.util.List<com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO> configs = new java.util.ArrayList<>();
        configs.add(new com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO().setRoleCode("CODER").setAgentName("Coder Agent").setSystemPrompt("You are a coder. Output success message."));
        configs.add(new com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO().setRoleCode("RESEARCHER").setAgentName("Researcher Agent").setSystemPrompt("You are a researcher."));
        configs.add(new com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO().setRoleCode("DESIGNER").setAgentName("Designer Agent").setSystemPrompt("You are a designer."));
        configs.add(new com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO().setRoleCode("COPYWRITER").setAgentName("Copywriter Agent").setSystemPrompt("You are a copywriter."));

        when(mockConfigMapper.selectList(Mockito.any())).thenReturn(configs);
        loaderService.initAgents(); // Load them into registry

        DagParserUtil dagParserUtil = new DagParserUtil(loaderService);

        // Assemble the Orchestrator with ALL REAL COMPONENTS except Persistence
//        orchestrator = new WorkflowOrchestrator(
//                plannerAgent, dagParserUtil, memoryService, checkpointService, eventLogService, messageBus, objectMapper
//        );
    }

    @Test
    public void testLiveModelMacroLoop() {
        String sessionId = "integration-test-session-" + UUID.randomUUID().toString();
        String goal = "Write a python script that prints hello world. Stop immediately after creating the script task.";
        
        System.out.println("Starting Integration Test for Goal: " + goal);
        orchestrator.executeMacroLoop(sessionId, goal);
        System.out.println("Integration Test Finished.");
    }
    
    @Test
    public void testComplexLiveModelMacroLoop() {
        String sessionId = "integration-complex-test-" + UUID.randomUUID().toString();
        String goal = "Marketing campaign for 'AI Smart Toaster'. " +
                      "Phase 1: RESEARCHER gathers data, DESIGNER creates logo concepts simultaneously. " +
                      "Phase 2: Once both DONE, COPYWRITER writes a slogan. " +
                      "Phase 3: Verify and STOP.";
                      
        System.out.println("Starting Complex Integration Test for Goal: " + goal);
        orchestrator.executeMacroLoop(sessionId, goal);
        System.out.println("Complex Integration Test Finished.");
    }
}
