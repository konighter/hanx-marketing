package com.hzltd.module.erplus.ai.mas.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a tool invocation request from the LLM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {

    /**
     * The tool name to invoke (must match {@link MasTool#getName()}).
     */
    private String toolName;

    /**
     * The parameters to pass to the tool.
     */
    private Map<String, Object> parameters;

    /**
     * Optional call ID for tracking (assigned by the LLM or execution engine).
     */
    private String callId;
}
