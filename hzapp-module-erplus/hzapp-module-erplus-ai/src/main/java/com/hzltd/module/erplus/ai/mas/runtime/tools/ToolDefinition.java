package com.hzltd.module.erplus.ai.mas.runtime.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Schema definition of a tool, suitable for rendering into LLM function-calling format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDefinition {

    private String name;
    private String description;
    private Map<String, ToolParam> parameters;

    /**
     * Renders this definition as a text prompt fragment for LLMs that don't support
     * native function calling (e.g. for prompt-based tool use).
     */
    public String toPromptDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("## ").append(name).append("\n");
        sb.append(description).append("\n");
        if (parameters != null && !parameters.isEmpty()) {
            sb.append("Parameters:\n");
            for (Map.Entry<String, ToolParam> entry : parameters.entrySet()) {
                ToolParam param = entry.getValue();
                sb.append("  - ").append(entry.getKey())
                  .append(" (").append(param.getType()).append(")")
                  .append(param.isRequired() ? " [REQUIRED]" : " [optional]")
                  .append(": ").append(param.getDescription()).append("\n");
            }
        }
        return sb.toString();
    }
}
