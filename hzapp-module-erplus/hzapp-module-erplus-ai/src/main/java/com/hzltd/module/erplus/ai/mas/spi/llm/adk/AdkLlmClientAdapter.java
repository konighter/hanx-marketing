package com.hzltd.module.erplus.ai.mas.spi.llm.adk;

import com.google.adk.agents.LlmAgent;
import com.google.adk.memory.BaseMemoryService;
import com.google.adk.runner.Runner;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.hzltd.module.erplus.ai.mas.spi.llm.MasLlmClient;
import com.hzltd.module.erplus.ai.mas.spi.llm.MasMessageHistory;

/**
 * Adapter that connects our MasLlmClient SPI to the ADK Runner.
 * It uses ADK's BaseMemoryService for internal memory operations.
 */
public class AdkLlmClientAdapter implements MasLlmClient {

    private final Runner runner;

    public AdkLlmClientAdapter(LlmAgent adkAgent, BaseMemoryService memoryService) {
        this.runner = Runner.builder()
                .agent(adkAgent)
                .appName("MAS-Framework")
                .memoryService(memoryService)
                .build();
    }

    @Override
    public String chat(MasMessageHistory history) {
        // Since ADK manages history using its own memory service internally, 
        // we might not pass standard message history directly to ADK yet.
        throw new UnsupportedOperationException("History-based chat not implemented for ADK adapter");
    }

    @Override
    public String chat(String prompt, String sessionId) {
        Content content = Content.fromParts(Part.fromText(prompt));
        String adkUserId = sessionId;

        runner.sessionService().createSession(adkUserId, sessionId).blockingGet();

        StringBuilder accumulatedResponse = new StringBuilder();

        // runAsync automatically calls the injected ADK memory service
        runner.runAsync(adkUserId, sessionId, content).blockingIterable().forEach(event -> {
            String snippet = event.stringifyContent();
            if (snippet != null) accumulatedResponse.append(snippet);
        });

        return accumulatedResponse.toString();
    }
}
