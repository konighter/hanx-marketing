package com.hzltd.module.erplus.ai.mas.runtime.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry for all MAS tools.
 * <p>
 * Auto-discovers all Spring beans implementing {@link MasTool} and registers them.
 * Provides lookup and execution capabilities for the {@code ReActNodeRunner}.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // Lookup and execute a tool
 * ToolResult result = toolRegistry.execute(
 *     ToolCall.builder().toolName("database_query").parameters(Map.of("sql", "SELECT 1")).build()
 * );
 *
 * // Get all tool definitions for LLM function-calling
 * List<ToolDefinition> defs = toolRegistry.getToolDefinitions();
 * }</pre>
 */
@Slf4j
@Service
public class ToolRegistry {

    private final Map<String, MasTool> tools = new ConcurrentHashMap<>();
    private final List<MasTool> discoveredTools;

    public ToolRegistry(List<MasTool> discoveredTools) {
        this.discoveredTools = discoveredTools != null ? discoveredTools : Collections.emptyList();
    }

    @PostConstruct
    public void init() {
        for (MasTool tool : discoveredTools) {
            register(tool);
        }
        log.info("[ToolRegistry] Initialized with {} tools: {}", tools.size(), tools.keySet());
    }

    /**
     * Register a tool. Overwrites if a tool with the same name already exists.
     */
    public void register(MasTool tool) {
        tools.put(tool.getName(), tool);
        log.debug("[ToolRegistry] Registered tool: {} ({})", tool.getName(), tool.getDescription());
    }

    /**
     * Execute a tool call.
     *
     * @param call The tool call request.
     * @return The tool execution result.
     */
    public ToolResult execute(ToolCall call) {
        MasTool tool = tools.get(call.getToolName());
        if (tool == null) {
            log.warn("[ToolRegistry] Unknown tool: {}", call.getToolName());
            return ToolResult.failure("Unknown tool: " + call.getToolName());
        }

        long start = System.currentTimeMillis();
        try {
            log.info("[ToolRegistry] Executing tool: {} with params: {}", call.getToolName(), call.getParameters());
            ToolResult result = tool.execute(call.getParameters());
            result.setDurationMs(System.currentTimeMillis() - start);
            log.debug("[ToolRegistry] Tool {} completed in {}ms, success={}", 
                    call.getToolName(), result.getDurationMs(), result.isSuccess());
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("[ToolRegistry] Tool {} execution failed after {}ms", call.getToolName(), duration, e);
            return ToolResult.failure(e.getMessage(), duration);
        }
    }

    /**
     * Get a tool by name. Returns null if not found.
     */
    public MasTool getTool(String name) {
        return tools.get(name);
    }

    /**
     * Get all registered tool names.
     */
    public Set<String> getToolNames() {
        return Collections.unmodifiableSet(tools.keySet());
    }

    /**
     * Get tool definitions formatted for LLM function-calling prompts.
     * Returns a list of definitions detailing each tool's name, description, and parameters.
     */
    public List<ToolDefinition> getToolDefinitions() {
        List<ToolDefinition> definitions = new ArrayList<>();
        for (MasTool tool : tools.values()) {
            definitions.add(ToolDefinition.builder()
                    .name(tool.getName())
                    .description(tool.getDescription())
                    .parameters(tool.getParameters())
                    .build());
        }
        return definitions;
    }

    /**
     * Get tool definitions filtered by a specific set of tool names.
     */
    public List<ToolDefinition> getToolDefinitions(Set<String> toolNames) {
        List<ToolDefinition> definitions = new ArrayList<>();
        for (String name : toolNames) {
            MasTool tool = tools.get(name);
            if (tool != null) {
                definitions.add(ToolDefinition.builder()
                        .name(tool.getName())
                        .description(tool.getDescription())
                        .parameters(tool.getParameters())
                        .build());
            }
        }
        return definitions;
    }
}
