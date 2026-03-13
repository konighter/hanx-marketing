package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LocalNodeMemory;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryKeys;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasSessionMemory;
import static com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryKeys.TASK_INSTRUCTION;
import com.hzltd.module.erplus.ai.mas.runtime.tools.annotation.MasTool;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Wraps a MAS BaseAgent as an ADK compatible tool.
 * This implements the Coordinator/Dispatcher pattern by allowing
 * a parent LLM to natively invoke sub-agents by treating them as tools.
 */
@Slf4j
public class SubAgentToolWrapper {

    private final BaseAgent delegateAgent;
    private final LocalNodeMemory parentMemory;

    public SubAgentToolWrapper(BaseAgent delegateAgent, LocalNodeMemory parentMemory) {
        this.delegateAgent = delegateAgent;
        this.parentMemory = parentMemory;
    }

    /**
     * ADK Function that gets invoked by the Coordinator LLM when it decides to delegate a sub-task.
     */
    @MasTool("Delegate a specific sub-task to the specialized sub-agent. Provide the exact task description.")
    public String executeSubAgent(String subTaskInstruction) {
        log.info("[SubAgentTool] Coordinator delegating instruction '{}' to sub-agent: {}", 
                 subTaskInstruction, delegateAgent.getRoleName());
                 
        // Create an isolated memory space for the sub-agent
        LocalNodeMemory childMemory = new LocalNodeMemory(
                parentMemory.getNodeId() + "_sub_" + delegateAgent.getRoleName(), 
                parentMemory.getGlobalMemory());
                
        // Instead of overriding the agent's core instruction, the contextual task is passed
        // through memory so the agent can append it.
        childMemory.put(TASK_INSTRUCTION, subTaskInstruction);
        
        try {
            return delegateAgent.execute(childMemory);
        } catch (Exception e) {
            log.error("[SubAgentTool] Sub-agent {} failed execution", delegateAgent.getRoleName(), e);
            return "Task failed: " + e.getMessage();
        }
    }

    /**
     * Factory method to generate the ADK BaseTool instance dynamically.
     */
    public BaseTool toAdkTool() {
        try {
            Method m = this.getClass().getMethod("executeSubAgent", String.class);
            return FunctionTool.create(this, m);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to wrap SubAgent as ADK Tool", e);
        }
    }
}
