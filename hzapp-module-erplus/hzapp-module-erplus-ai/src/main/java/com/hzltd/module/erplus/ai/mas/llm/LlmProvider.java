package com.hzltd.module.erplus.ai.mas.llm;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.OllamaLlm;

public class LlmProvider {

    private static final BaseLlm DEFAULT_LLM = new OllamaLlm("qwen3.5:9b", "http://192.168.1.3:11434/v1");

    public static BaseLlm defaultLlm() {
        return DEFAULT_LLM;
    }


}
