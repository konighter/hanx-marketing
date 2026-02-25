package com.hzltd.module.erplus.ai.mas.core.orchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.erplus.ai.mas.framework.agent.MasAgent;
import com.hzltd.module.erplus.ai.mas.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.mas.framework.event.*;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasTask;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasTaskExecutor;
import com.hzltd.module.erplus.ai.mas.framework.state.MasState;
import com.hzltd.module.erplus.ai.mas.framework.state.MasStateMachine;
import com.hzltd.module.erplus.ai.mas.framework.state.SimpleMasStateMachine;
import com.hzltd.module.erplus.ai.service.mas.MasPersistenceService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    
    // 当前参与的 Agents (Role -> Agent)
    private final Map<MasRole, MasAgent> activeAgents = new ConcurrentHashMap<>();

    public MasOrchestrator(String sessionId, MasEventBus eventBus, MasTaskExecutor taskExecutor, MasPersistenceService persistenceService) {
        this.sessionId = sessionId;
        this.eventBus = eventBus;
        this.taskExecutor = taskExecutor;
        this.persistenceService = persistenceService;
        this.stateMachine = new SimpleMasStateMachine(sessionId, eventBus);
        this.context = new MasContext(sessionId);
        
        initSubscriptions();
    }

    private void initSubscriptions() {
        // 1. 监听任务完成事件 -> 反馈给 Manager 决策 (仅非内部业务任务触发)
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
     * 启动会话 (从管理者初始化开始)
     */
    public Mono<Void> start(String goal) {
        log.info("[Orchestrator] 会话 {} 启动，目标: {}", sessionId, goal);
        context.setVariable("TOP_GOAL", goal);
        
        return stateMachine.transitTo(MasState.INIT)
                .flatMap(state -> routeGoalToManager(goal))
                .then();
    }

    private Mono<Void> routeGoalToManager(String goal) {
        MasAgent manager = activeAgents.get(MasRole.MANAGER);
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
                
        return taskExecutor.execute(initTask, manager, context).then();
    }

    private Mono<Void> routeToManager(TaskFinishedEvent event) {
        log.info("[Orchestrator] 任务 {} 完成 (状态: {})，向 Manager 反馈进行决策", 
                event.getTask().getName(), event.getStatus());
        
        MasAgent manager = activeAgents.get(MasRole.MANAGER);
        if (manager == null) return Mono.empty();

        MasTask evaluationTask = MasTask.builder()
                .taskId("EVAL_" + event.getTask().getTaskId())
                .name("任务执行结果评估")
                .prompt(String.format("任务 [%s] 执行结果如下:\n%s\n状态: %s\n请给出下一步决策。", 
                        event.getTask().getName(), event.getResult(), event.getStatus()))
                .roleRequited(MasRole.MANAGER.name())
                .internal(true)
                .build();

        return taskExecutor.execute(evaluationTask, manager, context)
                .flatMap(decision -> processManagerDecision(decision))
                .then();
    }

    private Mono<Void> routeUserFeedbackToManager(UserFeedbackEvent event) {
        log.info("[Orchestrator] 收到用户反馈: {}，由 Manager 评估", event.getFeedback());
        MasAgent manager = activeAgents.get(MasRole.MANAGER);
        if (manager == null) return Mono.empty();

        MasTask feedbackTask = MasTask.builder()
                .taskId("FEEDBACK_" + System.currentTimeMillis())
                .name("用户反馈评估")
                .prompt("收到用户实时反馈:\n" + event.getFeedback() + "\n请根据反馈调整计划或给出建议。")
                .roleRequited(MasRole.MANAGER.name())
                .internal(true)
                .build();

        return taskExecutor.execute(feedbackTask, manager, context)
                .flatMap(decision -> processManagerDecision(decision))
                .then();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 解析 Manager 的输出决策并执行相应的状态流转或任务指派
     */
    private Mono<Void> processManagerDecision(String decisionJson) {
        log.info("[Orchestrator] Manager 决策产出: {}", decisionJson);
        try {
            JsonNode node = objectMapper.readTree(decisionJson);
            String nextStep = node.has("next_step") ? node.get("next_step").asText() : "";
            
            if ("PLANNING".equalsIgnoreCase(nextStep)) {
                return stateMachine.transitTo(MasState.PLANNING).then();
            } else if ("EXECUTING".equalsIgnoreCase(nextStep)) {
                return stateMachine.transitTo(MasState.EXECUTING).then();
            } else if ("COMPLETED".equalsIgnoreCase(nextStep)) {
                return stateMachine.transitTo(MasState.COMPLETED).then();
            }
        } catch (Exception e) {
            log.error("[Orchestrator] 解析 Manager 决策失败: {}", decisionJson, e);
        }
        return Mono.empty();
    }

    public void registerAgent(MasAgent agent) {
        activeAgents.put(agent.getRole(), agent);
    }
}
