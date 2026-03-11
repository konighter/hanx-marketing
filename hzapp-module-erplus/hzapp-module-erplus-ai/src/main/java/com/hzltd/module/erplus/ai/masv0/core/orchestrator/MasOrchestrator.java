package com.hzltd.module.erplus.ai.masv0.core.orchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.erplus.ai.masv0.framework.agent.MasAgent;
import com.hzltd.module.erplus.ai.masv0.framework.agent.MasAgentRegistry;
import com.hzltd.module.erplus.ai.masv0.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.masv0.framework.event.*;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasTask;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasTaskExecutor;
import com.hzltd.module.erplus.ai.masv0.framework.state.MasState;
import com.hzltd.module.erplus.ai.masv0.framework.state.MasStateMachine;
import com.hzltd.module.erplus.ai.masv0.framework.state.SimpleMasStateMachine;
import com.hzltd.module.erplus.ai.service.mas.MasPersistenceService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MAS 核心编排器，管理整个会话的生命周期
 */
@Slf4j
public class MasOrchestrator {

    @Getter
    private final String sessionId;
    private final MasEventBus eventBus;
    private final MasStateMachine stateMachine;
    private final MasContext context;
    private final MasTaskExecutor taskExecutor;
    private final MasPersistenceService persistenceService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MasAgentRegistry agentRegistry;
    
    /** 用于从 LLM 输出中提取 JSON 代码块 */
    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("```(?:json)?\\s*\\n?(\\{.*?\\}|\\[.*?\\])\\s*\\n?```", Pattern.DOTALL);
    /** 备用：直接匹配 JSON 对象 */
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("(\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\})", Pattern.DOTALL);
    
    // 当前参与的 Agents (Role -> Agent) - 兼容旧版
    private final Map<MasRole, MasAgent> activeAgents = new ConcurrentHashMap<>();

    /**
     * 构造函数 - 兼容旧版
     */
    public MasOrchestrator(String sessionId, MasEventBus eventBus, MasTaskExecutor taskExecutor, MasPersistenceService persistenceService) {
        this(sessionId, eventBus, taskExecutor, persistenceService, null);
    }

    /**
     * 构造函数 - 支持动态 Agent 注册
     */
    public MasOrchestrator(String sessionId, MasEventBus eventBus, MasTaskExecutor taskExecutor, 
                           MasPersistenceService persistenceService, MasAgentRegistry agentRegistry) {
        this.sessionId = sessionId;
        this.eventBus = eventBus;
        this.taskExecutor = taskExecutor;
        this.persistenceService = persistenceService;
        this.agentRegistry = agentRegistry;
        this.stateMachine = new SimpleMasStateMachine(sessionId, eventBus);
        this.context = new MasContext(sessionId);
        
        initSubscriptions();
    }

    private void initSubscriptions() {
        // 1. 监听任务完成事件 -> 记录历史；对非内部任务，反馈给 Manager 决策
        eventBus.subscribe(TaskFinishedEvent.class)
                .filter(e -> e.getSessionId().equals(sessionId))
                .doOnNext(e -> persistenceService.recordTaskHistory(sessionId, e.getTask(), e.getResult(), e.getStatus().name(), e.getExecutionTime()))
                .filter(e -> !e.getTask().isInternal())
                .flatMap(this::routeToManager)
                .subscribe();

        // 2. 监听用户反馈事件 -> 反馈给 Manager 决策
        eventBus.subscribe(UserFeedbackEvent.class)
                .filter(e -> e.getSessionId().equals(sessionId))
                .flatMap(this::routeUserFeedbackToManager)
                .subscribe();
                
        // 3. 监听状态变更记录日志并持久化
        stateMachine.onStateChanged((from, to) -> {
            log.info("[Orchestrator] 会话 {} 状态变更: {} -> {}", sessionId, from.getLabel(), to.getLabel());
            persistenceService.saveOrUpdateSession(sessionId, (String) context.getVariable("TOP_GOAL"), to);
        });
    }

    /**
     * 从注册中心或本地 Map 获取 Agent
     */
    private MasAgent resolveAgent(MasRole role) {
        if (role == null) return null;
        String roleId = role.name();
        
        if (agentRegistry != null) {
            Optional<MasAgent> fromRegistry = agentRegistry.getAgent(roleId);
            if (fromRegistry.isPresent()) {
                return fromRegistry.get();
            }
        }
        
        return activeAgents.get(role);
    }

    /**
     * 启动会话 (从管理者初始化开始)
     */
    public Mono<Void> start(String goal) {
        log.info("[Orchestrator] 会话 {} 启动，目标: {}", sessionId, goal);
        context.setVariable("TOP_GOAL", goal);
        
        return stateMachine.transitTo(MasState.INIT)
                .flatMap(state -> routeGoalToManager(goal))
                .then();
    }

    /**
     * 初始化：将用户目标交给 Manager 进行分析和决策
     */
    private Mono<Void> routeGoalToManager(String goal) {
        MasAgent manager = resolveAgent(MasRole.MANAGER);
        if (manager == null) {
            log.error("[Orchestrator] 会话 {} 未配置 MANAGER Role", sessionId);
            return Mono.empty();
        }
        
        MasTask initTask = MasTask.builder()
                .taskId("INIT_" + sessionId)
                .name("初始需求分析与角色选定")
                .prompt("用户目标: " + goal + "\n请确定参与角色与项目流程。")
                .roleRequited(MasRole.MANAGER.name())
                .internal(true)
                .build();
        
        // 处理 Manager 的决策结果，而不是丢弃
        return taskExecutor.execute(initTask, manager, context)
                .flatMap(this::processManagerDecision)
                .then();
    }

    /**
     * 非内部任务完成后，反馈给 Manager 进行评估和下一步决策
     */
    private Mono<Void> routeToManager(TaskFinishedEvent event) {
        log.info("[Orchestrator] 任务 {} 完成 (状态: {})，向 Manager 反馈进行决策", 
                event.getTask().getName(), event.getStatus());
        
        MasAgent manager = resolveAgent(MasRole.MANAGER);
        if (manager == null) return Mono.empty();

        MasTask evaluationTask = MasTask.builder()
                .taskId("EVAL_" + event.getTask().getTaskId())
                .name("任务执行结果评估")
                .prompt(String.format("""
                        任务 [%s] 执行结果如下:
                        %s
                        状态: %s
                        
                        请评估任务结果并决定下一步。使用以下 JSON 格式输出:
                        {"next_step": "EXECUTING" 或 "COMPLETED", "task_prompt": "如需续行则填写下一个任务的指令"}
                        """, 
                        event.getTask().getName(), event.getResult(), event.getStatus()))
                .roleRequited(MasRole.MANAGER.name())
                .internal(true)
                .build();

        return taskExecutor.execute(evaluationTask, manager, context)
                .flatMap(this::processManagerDecision)
                .then();
    }

    /**
     * 用户反馈 -> Manager 评估
     */
    private Mono<Void> routeUserFeedbackToManager(UserFeedbackEvent event) {
        log.info("[Orchestrator] 收到用户反馈: {}，由 Manager 评估", event.getFeedback());
        MasAgent manager = resolveAgent(MasRole.MANAGER);
        if (manager == null) return Mono.empty();

        MasTask feedbackTask = MasTask.builder()
                .taskId("FEEDBACK_" + System.currentTimeMillis())
                .name("用户反馈评估")
                .prompt("收到用户实时反馈:\n" + event.getFeedback() + "\n请根据反馈调整计划或给出建议。\n" +
                        "请以 JSON 格式输出: {\"next_step\": \"PLANNING/EXECUTING/COMPLETED\", \"task_prompt\": \"具体指令\"}")
                .roleRequited(MasRole.MANAGER.name())
                .internal(true)
                .build();

        return taskExecutor.execute(feedbackTask, manager, context)
                .flatMap(this::processManagerDecision)
                .then();
    }

    // =============== 任务派发到具体 Agent ===============

    /**
     * 向 PM 派发规划任务：让 PM 将用户目标拆解为子任务列表
     */
    private Mono<Void> routeToPm() {
        MasAgent pm = resolveAgent(MasRole.PM);
        if (pm == null) {
            log.warn("[Orchestrator] 会话 {} 未配置 PM Role，跳过规划阶段", sessionId);
            return Mono.empty();
        }

        String goal = context.getVariable("TOP_GOAL");
        MasTask planTask = MasTask.builder()
                .taskId("PLAN_" + sessionId + "_" + System.currentTimeMillis())
                .name("任务规划与拆解")
                .prompt("请根据以下目标进行任务拆解:\n" + goal + "\n\n" +
                        "请以 JSON 数组格式输出任务列表，每个任务包含 name 和 prompt 字段:\n" +
                        "[{\"name\": \"任务名称\", \"prompt\": \"执行指令\"}]")
                .roleRequited(MasRole.PM.name())
                .internal(false)  // 非内部任务，完成后会触发 Manager 评估
                .build();

        return taskExecutor.execute(planTask, pm, context)
                .doOnSuccess(result -> context.setVariable("TASK_PLAN", result))
                .then();
    }

    /**
     * 向 Executor 派发执行任务：根据任务提示执行具体工作
     */
    private Mono<Void> routeToExecutor(String taskPrompt) {
        MasAgent executor = resolveAgent(MasRole.EXECUTOR);
        if (executor == null) {
            log.warn("[Orchestrator] 会话 {} 未配置 EXECUTOR Role", sessionId);
            return Mono.empty();
        }

        // 尝试从 taskPrompt 或上下文中的 TASK_PLAN 解析任务列表
        String tasksJson = taskPrompt != null && !taskPrompt.isBlank() ? taskPrompt : (String) context.getVariable("TASK_PLAN");
        
        if (tasksJson != null) {
            try {
                String extracted = extractJson(tasksJson);
                JsonNode tasksNode = objectMapper.readTree(extracted);
                
                if (tasksNode.isArray() && !tasksNode.isEmpty()) {
                    AtomicInteger index = new AtomicInteger(0);
                    return Flux.fromIterable(() -> tasksNode.elements())
                            .concatMap(taskNode -> {
                                int idx = index.incrementAndGet();
                                String name = taskNode.has("name") ? taskNode.get("name").asText() : "子任务 " + idx;
                                String prompt = taskNode.has("prompt") ? taskNode.get("prompt").asText() : taskNode.toString();
                                
                                MasTask subTask = MasTask.builder()
                                        .taskId("EXEC_" + sessionId + "_" + idx + "_" + System.currentTimeMillis())
                                        .name(name)
                                        .prompt(prompt)
                                        .roleRequited(MasRole.EXECUTOR.name())
                                        .internal(false)
                                        .build();
                                
                                return taskExecutor.execute(subTask, executor, context);
                            })
                            .then();
                }
            } catch (Exception e) {
                log.warn("[Orchestrator] 解析任务列表失败，作为单任务执行: {}", e.getMessage());
            }
        }

        // 兜底：作为单个执行任务
        String goal = context.getVariable("TOP_GOAL");
        String prompt = (taskPrompt != null && !taskPrompt.isBlank()) ? taskPrompt : "请执行以下目标:\n" + goal;
        
        MasTask execTask = MasTask.builder()
                .taskId("EXEC_" + sessionId + "_" + System.currentTimeMillis())
                .name("执行任务")
                .prompt(prompt)
                .roleRequited(MasRole.EXECUTOR.name())
                .internal(false)
                .build();
        
        return taskExecutor.execute(execTask, executor, context).then();
    }

    // =============== 决策解析与处理 ===============

    /**
     * 解析 Manager 的输出决策并执行相应的状态流转和任务派发
     */
    private Mono<Void> processManagerDecision(String decisionRaw) {
        log.info("[Orchestrator] Manager 决策产出: {}", decisionRaw);
        
        try {
            String jsonStr = extractJson(decisionRaw);
            JsonNode node = objectMapper.readTree(jsonStr);
            String nextStep = node.has("next_step") ? node.get("next_step").asText() : "";
            String taskPrompt = node.has("task_prompt") ? node.get("task_prompt").asText() : "";
            
            if ("PLANNING".equalsIgnoreCase(nextStep)) {
                return stateMachine.transitTo(MasState.PLANNING)
                        .flatMap(s -> routeToPm());
            } else if ("EXECUTING".equalsIgnoreCase(nextStep)) {
                return stateMachine.transitTo(MasState.EXECUTING)
                        .flatMap(s -> routeToExecutor(taskPrompt));
            } else if ("COMPLETED".equalsIgnoreCase(nextStep)) {
                return stateMachine.transitTo(MasState.COMPLETED).then();
            } else {
                log.warn("[Orchestrator] 未识别的 next_step: {}, 尝试默认进入 PLANNING", nextStep);
                return stateMachine.transitTo(MasState.PLANNING)
                        .flatMap(s -> routeToPm());
            }
        } catch (Exception e) {
            log.error("[Orchestrator] 解析 Manager 决策失败，默认进入 PLANNING: {}", decisionRaw, e);
            // 解析失败时，默认进入 PLANNING 阶段以避免流程卡死
            return stateMachine.transitTo(MasState.PLANNING)
                    .flatMap(s -> routeToPm());
        }
    }

    /**
     * 从 LLM 原始输出中提取 JSON 内容（支持 markdown 代码块包裹）
     */
    private String extractJson(String raw) {
        if (raw == null || raw.isBlank()) return "{}";
        
        // 1. 尝试从 ```json ... ``` 代码块中提取
        Matcher blockMatcher = JSON_BLOCK_PATTERN.matcher(raw);
        if (blockMatcher.find()) {
            return blockMatcher.group(1).trim();
        }
        
        // 2. 尝试直接匹配 JSON 对象
        Matcher objectMatcher = JSON_OBJECT_PATTERN.matcher(raw);
        if (objectMatcher.find()) {
            return objectMatcher.group(1).trim();
        }
        
        // 3. 直接返回原文（可能本身就是 JSON）
        return raw.trim();
    }

    /**
     * 将 Agent 注册到当前会话中
     */
    public void registerAgent(MasAgent agent) {
        activeAgents.put(agent.getRole(), agent);
    }

    /**
     * 手动终止会话
     */
    public Mono<Void> stop() {
        log.info("[Orchestrator] 会话 {} 被手动终止", sessionId);
        return stateMachine.transitTo(MasState.FAILED).then();
    }
}
