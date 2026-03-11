package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.hzltd.module.erplus.ai.mas.runtime.communication.AgentMessage;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;

/**
 * The base interface for all MAS Agents in the new runtime.
 */
public interface BaseAgent {

    /**
     * Get the unique role or name of this agent.
     */
    String getRoleName();

    /**
     * Optional: Receive an A2A message from another agent.
     * @param message The incoming message.
     */
    void onMessage(AgentMessage message);

    /**
     * Get the instruction or prompt this agent is configured with.
     */
    String getInstruction();

    /**
     * Execute the agent's logic within the scoped memory.
     *
     * @param memory The scoped memory for this execution loop.
     * @return The agent's structured response or execution result.
     */
    String execute(LoopMemory memory);
}
