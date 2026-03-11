package com.hzltd.module.erplus.ai;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.BaseTool;
public class AdkApiTest {
    @org.junit.jupiter.api.Test
    public void test() {
        System.out.println("IS_TOOL " + BaseTool.class.isAssignableFrom(LlmAgent.class));
    }
}
