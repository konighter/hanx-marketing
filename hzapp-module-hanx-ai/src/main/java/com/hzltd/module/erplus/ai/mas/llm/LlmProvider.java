package com.hzltd.module.erplus.ai.mas.llm;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;

public class LlmProvider {

    //TODO-- 切换模型
    private static final BaseLlm DEFAULT_LLM = new Gemini("", "");

    public static BaseLlm defaultLlm() {
        return DEFAULT_LLM;
    }

}
