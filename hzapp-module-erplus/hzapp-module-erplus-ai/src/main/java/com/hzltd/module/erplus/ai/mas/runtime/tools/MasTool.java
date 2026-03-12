package com.hzltd.module.erplus.ai.mas.runtime.tools;

import java.util.Map;

/**
 * SPI interface for MAS tools.
 * <p>
 * Tools are capabilities that agents can invoke during execution, such as
 * database queries, API calls, file operations, and shell commands.
 * <p>
 * Implementations should be registered as Spring beans and will be
 * auto-discovered by {@link ToolRegistry}.
 *
 * <h3>Example Implementation</h3>
 * <pre>{@code
 * @Component
 * public class DatabaseQueryTool implements MasTool {
 *     public String getName() { return "database_query"; }
 *     public String getDescription() { return "Execute SQL query against the database"; }
 *     public Map<String, ToolParam> getParameters() { return Map.of("sql", ...); }
 *     public ToolResult execute(Map<String, Object> params) { ... }
 * }
 * }</pre>
 */
public interface MasTool {

    /**
     * Unique tool name used in LLM function-calling.
     * Should be snake_case, e.g. "database_query", "shell_command".
     */
    String getName();

    /**
     * Human-readable description for the LLM to understand when to use this tool.
     */
    String getDescription();

    /**
     * Parameter definitions for the tool.
     * Key = parameter name, Value = parameter metadata.
     */
    Map<String, ToolParam> getParameters();

    /**
     * Execute the tool with the given parameters.
     *
     * @param params The parameters passed by the LLM.
     * @return The result of the tool execution.
     */
    ToolResult execute(Map<String, Object> params);
}
