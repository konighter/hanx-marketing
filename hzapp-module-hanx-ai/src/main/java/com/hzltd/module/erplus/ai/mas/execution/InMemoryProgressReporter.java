package com.hzltd.module.erplus.ai.mas.execution;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation that stores steps in-memory and logs them.
 * <p>
 * In production, this could be replaced or extended to push via
 * SSE (Server-Sent Events) or WebSocket for real-time UI updates.
 */
@Slf4j
@Service
public class InMemoryProgressReporter implements ExecutionProgressReporter {

    /** Key = sessionId:nodeId, Value = ordered list of steps */
    private final Map<String, List<StepReport>> stepStore = new ConcurrentHashMap<>();

    @Override
    public void reportStep(StepReport step) {
        String key = step.getSessionId() + ":" + step.getNodeId();
        stepStore.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(step);

        // Log for observability
        switch (step.getType()) {
            case THINKING:
                log.info("[ReAct][{}/{}] Step {}: THINK → {}",
                        step.getSessionId(), step.getNodeId(), step.getStepNumber(),
                        truncate(step.getThought(), 200));
                break;
            case TOOL_CALL:
                log.info("[ReAct][{}/{}] Step {}: TOOL_CALL → {}({})",
                        step.getSessionId(), step.getNodeId(), step.getStepNumber(),
                        step.getToolCall().getToolName(), step.getToolCall().getParameters());
                break;
            case OBSERVATION:
                log.info("[ReAct][{}/{}] Step {}: OBSERVATION → success={}, output={}",
                        step.getSessionId(), step.getNodeId(), step.getStepNumber(),
                        step.getToolResult().isSuccess(),
                        truncate(step.getToolResult().toObservation(), 200));
                break;
            case FINAL_ANSWER:
                log.info("[ReAct][{}/{}] Step {}: FINAL_ANSWER → {}",
                        step.getSessionId(), step.getNodeId(), step.getStepNumber(),
                        truncate(step.getFinalAnswer(), 200));
                break;
            case ERROR:
                log.error("[ReAct][{}/{}] Step {}: ERROR → {}",
                        step.getSessionId(), step.getNodeId(), step.getStepNumber(),
                        step.getErrorMessage());
                break;
        }
    }

    @Override
    public List<StepReport> getSteps(String sessionId, String nodeId) {
        String key = sessionId + ":" + nodeId;
        return stepStore.getOrDefault(key, Collections.emptyList());
    }

    /**
     * Clear stored steps for a session (call on session eviction).
     */
    public void clearSession(String sessionId) {
        stepStore.entrySet().removeIf(e -> e.getKey().startsWith(sessionId + ":"));
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "(null)";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
