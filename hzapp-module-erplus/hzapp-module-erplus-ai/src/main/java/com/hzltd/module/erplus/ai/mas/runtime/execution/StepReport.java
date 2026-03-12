package com.hzltd.module.erplus.ai.mas.runtime.execution;

import com.hzltd.module.erplus.ai.mas.runtime.tools.ToolCall;
import com.hzltd.module.erplus.ai.mas.runtime.tools.ToolResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents a single step in a ReAct execution loop.
 * Used for progress reporting and auditing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepReport {

    public enum StepType {
        /** LLM reasoning/thinking output */
        THINKING,
        /** Tool invocation */
        TOOL_CALL,
        /** Tool execution result (observation) */
        OBSERVATION,
        /** Final answer produced */
        FINAL_ANSWER,
        /** Error occurred */
        ERROR
    }

    private String sessionId;
    private String nodeId;
    private int stepNumber;
    private StepType type;

    /** The reasoning text (for THINKING steps) */
    private String thought;

    /** The tool call details (for TOOL_CALL steps) */
    private ToolCall toolCall;

    /** The tool result (for OBSERVATION steps) */
    private ToolResult toolResult;

    /** The final answer text (for FINAL_ANSWER steps) */
    private String finalAnswer;

    /** Error message (for ERROR steps) */
    private String errorMessage;

    /** Timestamp of this step */
    @Builder.Default
    private String timestamp = Instant.now().toString();
}
