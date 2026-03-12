package com.hzltd.module.erplus.ai.mas.runtime.execution;

/**
 * Represents the discrete executable states within a graph node's lifecycle.
 */
public enum ExecutionNode {
    THINK,
    PLAN,
    EXECUTE,
    REVIEW,
    DONE,
    FAILED
}
