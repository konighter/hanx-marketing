package com.hzltd.module.erplus.ai.mas.runtime.execution;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Structured result of a full DAG execution pass in {@link DagExecutionEngine}.
 * <p>
 * Replaces the previous fire-and-forget execution model with explicit
 * error propagation. The caller (e.g. WorkflowOrchestrator) can inspect
 * {@link #isSuccess()}, iterate over {@link #getFailures()}, and decide
 * whether to retry, skip, or abort the macro-loop.
 */
@Data
@Builder
public class DagExecutionResult {

    /**
     * Overall execution outcome.
     */
    public enum Outcome {
        /** All nodes completed successfully. */
        ALL_SUCCESS,
        /** At least one node failed or was skipped due to failed dependency. */
        PARTIAL_FAILURE,
        /** Execution timed out before all nodes could finish. */
        TIMEOUT,
        /** Interrupted (e.g. by thread interruption or cancellation request). */
        INTERRUPTED,
        /** Deadlock detected — circular dependencies or unresolvable nodes. */
        DEADLOCK
    }

    private Outcome outcome;

    /** Total number of nodes in the graph. */
    private int totalNodes;

    /** Number of nodes that finished with SUCCESS. */
    private int successCount;

    /** Number of nodes that finished with FAILED status. */
    private int failedCount;

    /** Number of nodes that were SKIPPED due to upstream failures. */
    private int skippedCount;

    /** Wall-clock duration of the full graph execution pass (ms). */
    private long durationMs;

    /** Per-node failure details (empty if all succeeded). */
    @Builder.Default
    private List<NodeFailureDetail> failures = new ArrayList<>();

    public boolean isSuccess() {
        return outcome == Outcome.ALL_SUCCESS;
    }

    /**
     * Per-node failure information for diagnostic and retry decisions.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NodeFailureDetail {
        private String nodeId;
        private String agentRole;
        private String errorMessage;
        private int attemptsMade;
    }
}
