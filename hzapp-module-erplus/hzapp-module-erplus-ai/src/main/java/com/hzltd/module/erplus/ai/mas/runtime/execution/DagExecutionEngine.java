package com.hzltd.module.erplus.ai.mas.runtime.execution;

import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * Event-driven DAG execution engine using {@link CompletionService} to
 * replace the original busy-wait polling loop.
 *
 * <h3>Design</h3>
 * <ol>
 *   <li>Scan nodes whose dependencies are all satisfied → submit via CompletionService</li>
 *   <li>Block on {@code completionService.poll(timeout)} until a result arrives</li>
 *   <li>Update node status → unlock downstream nodes → repeat</li>
 * </ol>
 *
 * <p>The engine does <b>not</b> own the thread pool; it receives a Spring-managed
 * {@link ExecutorService} (from {@link MasExecutionConfig}) via constructor injection.</p>
 */
@Slf4j
public class DagExecutionEngine {

    private final GlobalSessionMemory sessionMemory;
    private final Map<String, GraphNode> nodes = new ConcurrentHashMap<>();
    private final MasCheckpointService checkpointService;
    private final MasMemoryService memoryService;
    private final MasEventLogService eventLogService;
    private final ExecutorService executor;
    private final NodeRunner nodeRunner;
    private final NodeRunner reactNodeRunner;

    /** Default graph-level timeout: 10 minutes. */
    private static final long DEFAULT_GRAPH_TIMEOUT_MS = 600_000L;

    /** Poll interval when waiting for next node completion. */
    private static final long POLL_INTERVAL_MS = 500L;

    public DagExecutionEngine(GlobalSessionMemory sessionMemory,
                              MasCheckpointService checkpointService,
                              MasMemoryService memoryService,
                              MasEventLogService eventLogService,
                              ExecutorService executor,
                              NodeRunner nodeRunner,
                              NodeRunner reactNodeRunner) {
        this.sessionMemory = sessionMemory;
        this.checkpointService = checkpointService;
        this.memoryService = memoryService;
        this.eventLogService = eventLogService;
        this.executor = executor;
        this.nodeRunner = nodeRunner;
        this.reactNodeRunner = reactNodeRunner;
    }

    /**
     * Backward-compatible constructor (no ReAct runner).
     */
    public DagExecutionEngine(GlobalSessionMemory sessionMemory,
                              MasCheckpointService checkpointService,
                              MasMemoryService memoryService,
                              MasEventLogService eventLogService,
                              ExecutorService executor,
                              NodeRunner nodeRunner) {
        this(sessionMemory, checkpointService, memoryService, eventLogService, executor, nodeRunner, null);
    }

    public void addNode(GraphNode node) {
        nodes.put(node.getNodeId(), node);
    }

    // ---- legacy void executeGraph() kept for backward compat ----

    /**
     * @deprecated Use {@link #execute()} which returns a structured {@link DagExecutionResult}.
     */
    @Deprecated
    public void executeGraph() {
        DagExecutionResult result = execute();
        if (!result.isSuccess()) {
            throw new RuntimeException("Graph execution completed with failures: " + result.getOutcome()
                    + " (" + result.getFailedCount() + " failed, " + result.getSkippedCount() + " skipped)");
        }
    }

    /**
     * Execute the full DAG, returning a structured {@link DagExecutionResult}.
     */
    public DagExecutionResult execute() {
        log.info("[DagExecutionEngine] Starting graph execution for session: {}, nodes: {}",
                sessionMemory.getSessionId(), nodes.size());

        long startTime = System.currentTimeMillis();
        int totalNodes = nodes.size();

        CompletionService<NodeCompletionEvent> completionService =
                new ExecutorCompletionService<>(executor);

        // Track which nodes are in-flight
        Set<String> submitted = new HashSet<>();
        Set<String> completed = new HashSet<>();
        List<DagExecutionResult.NodeFailureDetail> failures = new ArrayList<>();
        int activeTasks = 0;

        try {
            while (completed.size() < totalNodes) {
                // ---------- 1. Dispatch ready nodes ----------
                boolean dispatched = dispatchReady(completionService, submitted, completed);

                // ---------- 2. Deadlock detection ----------
                if (!dispatched && activeTasks == 0 && completed.size() < totalNodes) {
                    log.error("[DagExecutionEngine] Deadlock detected! completed={}, total={}",
                            completed.size(), totalNodes);
                    return buildResult(DagExecutionResult.Outcome.DEADLOCK, startTime, failures);
                }

                // ---------- 3. Wait for next node to finish ----------
                if (activeTasks > 0 || dispatched) {
                    // recalculate active count after dispatch
                    activeTasks = submitted.size() - completed.size();

                    long remaining = DEFAULT_GRAPH_TIMEOUT_MS - (System.currentTimeMillis() - startTime);
                    if (remaining <= 0) {
                        log.error("[DagExecutionEngine] Timeout after {}ms", DEFAULT_GRAPH_TIMEOUT_MS);
                        return buildResult(DagExecutionResult.Outcome.TIMEOUT, startTime, failures);
                    }

                    Future<NodeCompletionEvent> future =
                            completionService.poll(Math.min(POLL_INTERVAL_MS, remaining), TimeUnit.MILLISECONDS);

                    if (future != null) {
                        NodeCompletionEvent event = future.get(); // will not block – already done
                        completed.add(event.nodeId);
                        activeTasks = submitted.size() - completed.size();

                        if (!event.success) {
                            failures.add(DagExecutionResult.NodeFailureDetail.builder()
                                    .nodeId(event.nodeId)
                                    .agentRole(event.agentRole)
                                    .errorMessage(event.errorMessage)
                                    .attemptsMade(event.attempts)
                                    .build());
                        }
                        log.info("[DagExecutionEngine] Node completed: {} (success={}) [{}/{}]",
                                event.nodeId, event.success, completed.size(), totalNodes);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[DagExecutionEngine] Interrupted during graph execution");
            return buildResult(DagExecutionResult.Outcome.INTERRUPTED, startTime, failures);
        } catch (ExecutionException e) {
            // should not reach here since we catch inside NodeCompletionEvent
            log.error("[DagExecutionEngine] Unexpected ExecutionException", e);
            return buildResult(DagExecutionResult.Outcome.PARTIAL_FAILURE, startTime, failures);
        }

        DagExecutionResult.Outcome outcome = failures.isEmpty()
                ? DagExecutionResult.Outcome.ALL_SUCCESS
                : DagExecutionResult.Outcome.PARTIAL_FAILURE;

        log.info("[DagExecutionEngine] Graph execution finished: outcome={}, duration={}ms",
                outcome, System.currentTimeMillis() - startTime);
        return buildResult(outcome, startTime, failures);
    }

    // ========== internal helpers ==========

    /**
     * Scan nodes whose prerequisites are all met, submit them, and skip
     * nodes whose upstream dependencies have failed.
     *
     * @return true if at least one new node was submitted or skipped
     */
    private boolean dispatchReady(CompletionService<NodeCompletionEvent> cs,
                                   Set<String> submitted,
                                   Set<String> completed) {
        boolean progress = false;

        for (GraphNode node : nodes.values()) {
            String nid = node.getNodeId();
            if (submitted.contains(nid)) continue; // already dispatched or finished

            // -- Check upstream failures → SKIP this node --
            boolean upstreamFailed = false;
            for (String depId : node.getRequires()) {
                GraphNode dep = nodes.get(depId);
                if (dep != null && (dep.getStatus() == GraphNode.NodeStatus.FAILED
                        || dep.getStatus() == GraphNode.NodeStatus.SKIPPED)) {
                    upstreamFailed = true;
                    break;
                }
            }
            if (upstreamFailed) {
                node.setStatus(GraphNode.NodeStatus.SKIPPED);
                submitted.add(nid);
                completed.add(nid);
                log.info("[DagExecutionEngine] Skipping node {} due to upstream failure", nid);
                progress = true;
                continue;
            }

            // -- Check all deps are SUCCESS → dispatch --
            boolean ready = true;
            for (String depId : node.getRequires()) {
                GraphNode dep = nodes.get(depId);
                if (dep == null || dep.getStatus() != GraphNode.NodeStatus.SUCCESS) {
                    ready = false;
                    break;
                }
            }

            if (ready) {
                log.info("[DagExecutionEngine] Dispatching node: {}", nid);
                submitted.add(nid);
                cs.submit(() -> executeNode(node));
                progress = true;
            }
        }
        return progress;
    }

    /**
     * Wrapper that delegates to {@link NodeExecutor} and returns a
     * completion event regardless of success or failure.
     */
    private NodeCompletionEvent executeNode(GraphNode node) {
        NodeExecutor runner = new NodeExecutor(
                node, sessionMemory, checkpointService, memoryService, eventLogService, nodeRunner, reactNodeRunner);
        try {
            String result = runner.call();
            return NodeCompletionEvent.success(node.getNodeId(), node.getAgent().getRoleName(), result);
        } catch (Exception e) {
            return NodeCompletionEvent.failure(node.getNodeId(), node.getAgent().getRoleName(), e.getMessage(), 0);
        }
    }

    private DagExecutionResult buildResult(DagExecutionResult.Outcome outcome,
                                            long startTime,
                                            List<DagExecutionResult.NodeFailureDetail> failures) {
        int success = 0, failed = 0, skipped = 0;
        for (GraphNode gn : nodes.values()) {
            switch (gn.getStatus()) {
                case SUCCESS:  success++;  break;
                case FAILED:   failed++;   break;
                case SKIPPED:  skipped++;  break;
                default: break;
            }
        }
        return DagExecutionResult.builder()
                .outcome(outcome)
                .totalNodes(nodes.size())
                .successCount(success)
                .failedCount(failed)
                .skippedCount(skipped)
                .durationMs(System.currentTimeMillis() - startTime)
                .failures(failures)
                .build();
    }

    // ---- internal event ----

    /**
     * Lightweight event carrying the result of a single node execution.
     * Always produced (success or failure), so that {@link CompletionService#poll}
     * never blocks indefinitely waiting for a future that threw.
     */
    private static class NodeCompletionEvent {
        final String nodeId;
        final String agentRole;
        final boolean success;
        final String result;
        final String errorMessage;
        final int attempts;

        private NodeCompletionEvent(String nodeId, String agentRole, boolean success,
                                     String result, String errorMessage, int attempts) {
            this.nodeId = nodeId;
            this.agentRole = agentRole;
            this.success = success;
            this.result = result;
            this.errorMessage = errorMessage;
            this.attempts = attempts;
        }

        static NodeCompletionEvent success(String nodeId, String agentRole, String result) {
            return new NodeCompletionEvent(nodeId, agentRole, true, result, null, 1);
        }

        static NodeCompletionEvent failure(String nodeId, String agentRole, String errorMessage, int attempts) {
            return new NodeCompletionEvent(nodeId, agentRole, false, null, errorMessage, attempts);
        }
    }
}
