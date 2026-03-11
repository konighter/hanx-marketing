package com.hzltd.module.erplus.ai.mas.runtime.loop;

/**
 * Represents the discrete executable states within an AgentLoop.
 */
public enum LoopNode {
    THINK,
    PLAN,
    EXECUTE,
    REVIEW,
    DONE,
    FAILED
}
