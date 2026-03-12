package com.hzltd.module.erplus.ai.mas.runtime.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result returned by a tool execution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResult {

    /**
     * Whether the tool execution succeeded.
     */
    private boolean success;

    /**
     * The output content (text result for the LLM to observe).
     */
    private String output;

    /**
     * Error message if execution failed.
     */
    private String error;

    /**
     * Execution duration in milliseconds.
     */
    private long durationMs;

    // --- Convenience factory methods ---

    public static ToolResult success(String output) {
        return ToolResult.builder().success(true).output(output).build();
    }

    public static ToolResult success(String output, long durationMs) {
        return ToolResult.builder().success(true).output(output).durationMs(durationMs).build();
    }

    public static ToolResult failure(String error) {
        return ToolResult.builder().success(false).error(error).build();
    }

    public static ToolResult failure(String error, long durationMs) {
        return ToolResult.builder().success(false).error(error).durationMs(durationMs).build();
    }

    /**
     * Returns a formatted string suitable for inclusion in the LLM context.
     */
    public String toObservation() {
        if (success) {
            return output != null ? output : "(empty result)";
        } else {
            return "ERROR: " + (error != null ? error : "Unknown error");
        }
    }
}
