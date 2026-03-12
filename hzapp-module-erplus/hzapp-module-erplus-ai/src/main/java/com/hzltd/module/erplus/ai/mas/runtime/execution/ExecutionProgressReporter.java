package com.hzltd.module.erplus.ai.mas.runtime.execution;

import java.util.List;

/**
 * Interface for reporting execution progress from ReAct agents.
 * <p>
 * Implementations may push to WebSocket, SSE, event bus, or simply log.
 */
public interface ExecutionProgressReporter {

    /**
     * Report a single execution step.
     */
    void reportStep(StepReport step);

    /**
     * Get all steps reported for a given session+node combination.
     */
    List<StepReport> getSteps(String sessionId, String nodeId);
}
