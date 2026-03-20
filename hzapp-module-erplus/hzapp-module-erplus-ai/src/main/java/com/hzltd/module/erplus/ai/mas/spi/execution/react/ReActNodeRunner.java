package com.hzltd.module.erplus.ai.mas.spi.execution.react;


import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.ai.mas.execution.ExecutionProgressReporter;
import com.hzltd.module.erplus.ai.mas.execution.GraphNode;
import com.hzltd.module.erplus.ai.mas.execution.StepReport;
import com.hzltd.module.erplus.ai.mas.spi.execution.NodeRunner;
import com.hzltd.module.erplus.ai.mas.spi.memory.NodeMemory;
import com.hzltd.module.erplus.ai.mas.tools.ToolCall;
import com.hzltd.module.erplus.ai.mas.tools.ToolDefinition;
import com.hzltd.module.erplus.ai.mas.tools.ToolRegistry;
import com.hzltd.module.erplus.ai.mas.tools.ToolResult;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ReAct (Reasoning + Acting) NodeRunner.
 */
@Slf4j
public class ReActNodeRunner implements NodeRunner {

    private final ToolRegistry toolRegistry;
    private final ExecutionProgressReporter progressReporter;
    private final ReActLlmClient llmClient;
    private final int maxSteps;

    public ReActNodeRunner(ToolRegistry toolRegistry,
                           ExecutionProgressReporter progressReporter,
                           ReActLlmClient llmClient,
                           int maxSteps) {
        this.toolRegistry = toolRegistry;
        this.progressReporter = progressReporter;
        this.llmClient = llmClient;
        this.maxSteps = maxSteps;
    }

    public ReActNodeRunner(ToolRegistry toolRegistry,
                           ExecutionProgressReporter progressReporter,
                           ReActLlmClient llmClient) {
        this(toolRegistry, progressReporter, llmClient, 25);
    }

    @Override
    public String run(GraphNode node, NodeMemory memory) throws Exception {
        String sessionId = memory.getSessionId();
        String nodeId = node.getNodeId();

        Set<String> allowedTools = node.getToolSet();
        List<ToolDefinition> toolDefs = (allowedTools != null && !allowedTools.isEmpty())
                ? toolRegistry.getToolDefinitions(allowedTools)
                : toolRegistry.getToolDefinitions();

        String systemPrompt = buildSystemPrompt(node, memory, toolDefs);

        List<ReActLlmClient.Message> conversation = new ArrayList<>();
        conversation.add(ReActLlmClient.Message.system(systemPrompt));

        String taskInstruction = memory.get("taskInstruction") != null
                ? memory.get("taskInstruction").toString()
                : (node.getAgent() != null ? node.getAgent().getInstruction() : "No instruction");
        conversation.add(ReActLlmClient.Message.user(taskInstruction));

        log.info("[ReActNodeRunner] Starting ReAct loop for node {} with {} tools, max {} steps",
                nodeId, toolDefs.size(), maxSteps);

        for (int step = 1; step <= maxSteps; step++) {
            String llmResponse = llmClient.generate(conversation);
            ReActResponse parsed = parseResponse(llmResponse);

            if (parsed.thought != null) {
                progressReporter.reportStep(StepReport.builder()
                        .sessionId(sessionId).nodeId(nodeId)
                        .stepNumber(step).type(StepReport.StepType.THINKING)
                        .thought(parsed.thought).build());
            }

            if (parsed.finalAnswer != null) {
                progressReporter.reportStep(StepReport.builder()
                        .sessionId(sessionId).nodeId(nodeId)
                        .stepNumber(step).type(StepReport.StepType.FINAL_ANSWER)
                        .finalAnswer(parsed.finalAnswer).build());

                log.info("[ReActNodeRunner] Node {} completed in {} steps", nodeId, step);
                return parsed.finalAnswer;
            }

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

                ToolResult result = toolRegistry.execute(toolCall);

                progressReporter.reportStep(StepReport.builder()
                        .sessionId(sessionId).nodeId(nodeId)
                        .stepNumber(step).type(StepReport.StepType.OBSERVATION)
                        .toolResult(result).build());

                conversation.add(ReActLlmClient.Message.assistant(llmResponse));
                conversation.add(ReActLlmClient.Message.user(
                        "Observation: " + result.toObservation()));
            } else {
                conversation.add(ReActLlmClient.Message.assistant(llmResponse));
                conversation.add(ReActLlmClient.Message.user(
                        "Please provide either a tool action or your final_answer."));
            }
        }

        String errorMsg = "ReAct loop exceeded maximum steps (" + maxSteps + ") for node " + nodeId;
        progressReporter.reportStep(StepReport.builder()
                .sessionId(sessionId).nodeId(nodeId)
                .stepNumber(maxSteps + 1).type(StepReport.StepType.ERROR)
                .errorMessage(errorMsg).build());

        log.warn("[ReActNodeRunner] {}", errorMsg);
        throw new RuntimeException(errorMsg);
    }

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

    private ReActResponse parseResponse(String llmOutput) {

        if (llmOutput == null || llmOutput.isBlank()) return new ReActResponse();

        String cleaned = llmOutput.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        else if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        cleaned = cleaned.trim();

        ReActResponse response  = JsonUtils.parseObject(cleaned, ReActResponse.class);
        if (response == null) {
            response = new ReActResponse();
            response.thought = cleaned;
        }

        return response;
    }

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
