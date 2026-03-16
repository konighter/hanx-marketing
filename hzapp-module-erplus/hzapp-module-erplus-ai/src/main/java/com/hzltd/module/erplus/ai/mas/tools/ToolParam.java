package com.hzltd.module.erplus.ai.mas.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata describing a single tool parameter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolParam {

    /**
     * Parameter data type: "string", "integer", "boolean", "object", "array".
     */
    private String type;

    /**
     * Human-readable description for the LLM.
     */
    private String description;

    /**
     * Whether this parameter is required.
     */
    @Builder.Default
    private boolean required = true;

    /**
     * Default value if not provided.
     */
    private Object defaultValue;

    // --- Convenience factory methods ---

    public static ToolParam string(String description) {
        return ToolParam.builder().type("string").description(description).required(true).build();
    }

    public static ToolParam string(String description, boolean required) {
        return ToolParam.builder().type("string").description(description).required(required).build();
    }

    public static ToolParam integer(String description) {
        return ToolParam.builder().type("integer").description(description).required(true).build();
    }

    public static ToolParam bool(String description) {
        return ToolParam.builder().type("boolean").description(description).required(false).build();
    }
}
