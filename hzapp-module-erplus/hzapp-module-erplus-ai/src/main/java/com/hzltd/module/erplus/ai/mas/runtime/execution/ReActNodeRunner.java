package com.hzltd.module.erplus.ai.mas.runtime.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.erplus.ai.mas.runtime.memory.NodeMemory;
import com.hzltd.module.erplus.ai.mas.runtime.tools.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ReAct (Reasoning + Acting) NodeRunner.
 * <p>
 * Implements the Think → Act → Observe loop, similar to Antigravity's execution flow.
 * Unlike the simple {@link AdkNodeRunner} which does a single LLM call, this runner
 * enters a multi-step loop where the LLM can invoke tools and observe results
 * before producing a final answer.
 *
 * <h3>Execution Flow</h3>
 * <pre>
 * 1. Build context (instruction + available tools + memory)
 * 2. Loop:
 *    a. LLM generates response (thought + action OR final_answer)
 *    b. If final_answer → return result
 *    c. If tool_call → execute tool → append observation → continue loop
 * 3. Safety limit: max steps to prevent infinite loops
 * </pre>
 *
 * <h3>LLM Response Format</h3>
 * The LLM is prompted to respond in JSON:
 * <pre>{@code
 * // Tool call:
 * {"thought": "I need to query the database...", "action": {"tool": "database_query", "params": {"sql": "..."}}}
 *
 * // Final answer:
 * {"thought": "The task is complete.", "final_answer": "Here are the results..."}
 * }</pre>
 */
@Slf4j
public class ReActNodeRunner implements NodeRunner {

    private final ToolRegistry toolRegistry;
    private final ExecutionProgressReporter progressReporter;
    private final ReActLlmClient llmClient;
    private final ObjectMapper objectMapper;

    /** Maximum number of reasoning steps before forced termination. */
    private final int maxSteps;

    public ReActNodeRunner(ToolRegistry toolRegistry,
                           ExecutionProgressReporter progressReporter,
                           ReActLlmClient llmClient,
                           ObjectMapper objectMapper,
                           int maxSteps) {
        this.toolRegistry = toolRegistry;
        this.progressReporter = progressReporter;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
        this.maxSteps = maxSteps;
    }

    public ReActNodeRunner(ToolRegistry toolRegistry,
                           ExecutionProgressReporter progressReporter,
                           ReActLlmClient llmClient,
                           ObjectMapper objectMapper) {
        this(toolRegistry, progressReporter, llmClient, objectMapper, 25);
    }

    @Override
    public String run(GraphNode node, NodeMemory memory) throws Exception {
        String sessionId = memory.getSessionId();
        String nodeId = node.getNodeId();

        // 1. Build the system prompt with available tools
        Set<String> allowedTools = node.getToolSet();
        List<ToolDefinition> toolDefs = (allowedTools != null && !allowedTools.isEmpty())
                ? toolRegistry.getToolDefinitions(allowedTools)
                : toolRegistry.getToolDefinitions();

        String systemPrompt = buildSystemPrompt(node, memory, toolDefs);

        // 2. Conversation history for this ReAct loop
        List<ReActLlmClient.Message> conversation = new ArrayList<>();
        conversation.add(ReActLlmClient.Message.system(systemPrompt));

        // Add the task instruction as the first user message
        String taskInstruction = memory.get("taskInstruction") != null
                ? memory.get("taskInstruction").toString()
                : node.getAgent().getInstruction();
        conversation.add(ReActLlmClient.Message.user(taskInstruction));

        log.info("[ReActNodeRunner] Starting ReAct loop for node {} with {} tools, max {} steps",
                nodeId, toolDefs.size(), maxSteps);

        // 3. ReAct loop
        for (int step = 1; step <= maxSteps; step++) {
            // LLM generates response
            String llmResponse = llmClient.generate(conversation);

            // Parse the structured response
            ReActResponse parsed = parseResponse(llmResponse);

            // Report thinking step
            if (parsed.thought != null) {
                progressReporter.reportStep(StepReport.builder()
                        .sessionId(sessionId).nodeId(nodeId)
                        .stepNumber(step).type(StepReport.StepType.THINKING)
                        .thought(parsed.thought).build());
            }

            // Check if it's a final answer
            if (parsed.finalAnswer != null) {
                progressReporter.reportStep(StepReport.builder()
                        .sessionId(sessionId).nodeId(nodeId)
                        .stepNumber(step).type(StepReport.StepType.FINAL_ANSWER)
                        .finalAnswer(parsed.finalAnswer).build());

                log.info("[ReActNodeRunner] Node {} completed in {} steps", nodeId, step);
                return parsed.finalAnswer;
            }

            // It's a tool call
            if (parsed.action != null) {
                ToolCall toolCall = ToolCall.builder()
                        .toolName(parsed.action.tool)
                        .parameters(parsed.action.params)
                        .callId(nodeId + "_step_" + step)
                        .build();

                progressReporter.reportStep(StepReport.builder()
                        .sessionId(sessionId).nodeId(nodeId)
                        .stepNumber(step).type(StepReport.StepType.TOOL_CALL)
                        .toolCall(toolCall).build());

                // Execute the tool
                ToolResult result = toolRegistry.execute(toolCall);

                progressReporter.reportStep(StepReport.builder()
                        .sessionId(sessionId).nodeId(nodeId)
                        .stepNumber(step).type(StepReport.StepType.OBSERVATION)
                        .toolResult(result).build());

                // Append assistant response + observation to conversation
                conversation.add(ReActLlmClient.Message.assistant(llmResponse));
                conversation.add(ReActLlmClient.Message.user(
                        "Observation: " + result.toObservation()));
            } else {
                // LLM didn't provide action or final_answer — treat as thinking only
                conversation.add(ReActLlmClient.Message.assistant(llmResponse));
                conversation.add(ReActLlmClient.Message.user(
                        "Please provide either a tool action or your final_answer."));
            }
        }

        // Exceeded max steps
        String errorMsg = "ReAct loop exceeded maximum steps (" + maxSteps + ") for node " + nodeId;
        progressReporter.reportStep(StepReport.builder()
                .sessionId(sessionId).nodeId(nodeId)
                .stepNumber(maxSteps + 1).type(StepReport.StepType.ERROR)
                .errorMessage(errorMsg).build());

        log.warn("[ReActNodeRunner] {}", errorMsg);
        throw new RuntimeException(errorMsg);
    }

    // --- System prompt construction ---

    private String buildSystemPrompt(GraphNode node, NodeMemory memory, List<ToolDefinition> toolDefs) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an autonomous agent executing a task. ");
        sb.append("You have access to the following tools:\n\n");

        for (ToolDefinition def : toolDefs) {
            sb.append(def.toPromptDescription()).append("\n");
        }

        sb.append("## Response Format\n");
        sb.append("You MUST respond with a JSON object in one of these two formats:\n\n");
        sb.append("To use a tool:\n");
        sb.append("{\"thought\": \"your reasoning\", \"action\": {\"tool\": \"tool_name\", \"params\": {\"param1\": \"value1\"}}}\n\n");
        sb.append("To provide your final answer:\n");
        sb.append("{\"thought\": \"your final reasoning\", \"final_answer\": \"your complete answer\"}\n\n");
        sb.append("IMPORTANT: Output ONLY the JSON object, no markdown formatting.\n");

        return sb.toString();
    }

    // --- Response parsing ---

    private ReActResponse parseResponse(String llmOutput) {
        ReActResponse response = new ReActResponse();

        if (llmOutput == null || llmOutput.isBlank()) {
            return response;
        }

        // Strip markdown code fences if present
        String cleaned = llmOutput.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        else if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        cleaned = cleaned.trim();

        try {
            JsonNode json = objectMapper.readTree(cleaned);

            if (json.has("thought")) {
                response.thought = json.get("thought").asText();
            }
            if (json.has("final_answer")) {
                response.finalAnswer = json.get("final_answer").asText();
            }
            if (json.has("action")) {
                JsonNode actionNode = json.get("action");
                response.action = new ReActAction();
                response.action.tool = actionNode.has("tool") ? actionNode.get("tool").asText() : null;
                if (actionNode.has("params")) {
                    response.action.params = objectMapper.convertValue(
                            actionNode.get("params"),
                            new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("[ReActNodeRunner] Failed to parse LLM response as JSON, treating as thought: {}", cleaned, e);
            response.thought = cleaned;
        }

        return response;
    }

    // --- Internal data structures ---

    private static class ReActResponse {
        String thought;
        ReActAction action;
        String finalAnswer;
    }

    private static class ReActAction {
        String tool;
        Map<String, Object> params;
    }
}
