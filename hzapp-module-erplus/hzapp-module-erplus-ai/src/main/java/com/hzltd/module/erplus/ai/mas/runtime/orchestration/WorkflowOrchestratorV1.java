package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

import com.google.adk.agents.BaseAgent;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.Session;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.ai.mas.runtime.agent.PlannerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.agent.ReviewerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagGenerationPlan;
import com.hzltd.module.erplus.ai.mas.runtime.report.MasEvent;
import com.hzltd.module.erplus.ai.mas.runtime.report.MasEventPublisher;
import com.hzltd.module.erplus.ai.mas.runtime.report.MasEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// TODO(runner.runAsync): re-add when implementing ADK execution:
//   import com.google.adk.events.Event;
//   import com.google.genai.types.Content;
//   import com.google.genai.types.Part;
//   import io.reactivex.rxjava3.core.Flowable;
//   import io.reactivex.rxjava3.disposables.Disposable;
//   import java.util.Map;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * WorkflowOrchestrator 重构版 (V1) — ADK Session 深度集成.
 *
 * <h2>与原版的核心区别</h2>
 * <ul>
 *   <li>一个 Session 贯穿整个 Task 生命周期（ADK {@link Runner} + {@link Session} 全程复用）</li>
 *   <li>DAG 执行由 {@link MasDagDispatcher} 动态组合 ADK Agent 图承载</li>
 *   <li>状态存储完全依赖 ADK {@code session.state}（替代 GlobalSessionMemory + saveToDb）</li>
 *   <li>事件通过 {@link MasEventPublisher} 双轨派发（DB 持久化 + WebSocket 实时推流）</li>
 *   <li>{@code sessionId} / {@code userId} 由外部 MasTaskJob 传入作为运行时参数</li>
 * </ul>
 *
 * <h2>依赖层次</h2>
 * <pre>
 * ┌─ Spring @Component 单例（构造注入）──────────────────────────────┐
 * │  Runner            — ADK 应用级执行器（持有 SessionService）     │
 * │  MasDagDispatcher  — DAG → ADK Agent 图动态组合器               │
 * │  MasEventPublisher — 双轨事件派发（DB + WebSocket），全局单例    │
 * │  InterruptManager  — URGENT/SUPPLEMENT 分级中断，全局单例        │
 * │  PlannerAgent      — 系统内置规划 Agent（@Component）            │
 * │  ReviewerAgent     — 系统内置审查 Agent（@Component）            │
 * └──────────────────────────────────────────────────────────────────┘
 * ┌─ 运行时参数（executeMacroLoop 方法入参）──────────────────────────┐
 * │  appName           — 父任务 → ADK App（Trace 整棵任务树 + Resume）│
 * │  sessionId         — 叶子任务 → ADK Session ID（具体执行单元）   │
 * │  userId            — 发起该 Task 的用户/租户 ID                  │
 * │  goal              — 格式化后的任务目标                           │
 * └──────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h2>实施策略</h2>
 * 原 {@link WorkflowOrchestrator} 保持不变。待本类稳定后，MasTaskJob 切换引用到此类。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowOrchestratorV1 {

    // ── Spring 单例（构造注入） ────────────────────────────────────────────────
    // 注：appName 对应父任务 ID，是运行时参数，从 executeMacroLoop 传入，非全局配置

    /** ADK Runner：持有 SessionService，全应用复用 */
    private final Runner runner;

    /** DAG 动态组合器：将 DagGenerationPlan 转换为 ADK Agent 图 */
    private final MasDagDispatcher dagDispatcher;

    /** 事件发布器：双轨派发（DB log + WebSocket），全局单例 */
    private final MasEventPublisher eventPublisher;

    /** 中断管理器：URGENT/SUPPLEMENT 分级中断，全局单例 */
    private final InterruptManager interruptManager;

    /**
     * 系统内置 Planner Agent.
     * <p>已被 Spring 管理为 {@code @Component}，直接注入，无需通过 Factory 获取。
     */
    private final PlannerAgent plannerAgent;

    /**
     * 系统内置 Reviewer Agent.
     * <p>已被 Spring 管理为 {@code @Component}，直接注入，无需通过 Factory 获取。
     */
    private final ReviewerAgent reviewerAgent;

    // ── 常量 ─────────────────────────────────────────────────────────────────

    private static final int MAX_PHASES = 50;
    private static final long PHASE_TIMEOUT_MINUTES = 5;

    // session.state key 约定（全局统一）
    static final String KEY_USER_GOAL          = "user_goal";
    static final String KEY_PHASE_COUNT        = "phase_count";
    static final String KEY_URGENT_INTERRUPT   = "urgent_interrupt";
    static final String KEY_PENDING_SUPPLEMENTS = "pending_supplements";
    static final String KEY_USER_APPROVAL      = "user_approval";

    // ─────────────────────────────────────────────────────────────────────────
    //  主入口
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * 执行多 Phase 宏循环.
     *
     * <p>由 {@code MasTaskJob} 调用，命名约定：
     * <pre>
     * // appName 对应父任务（MasTask 中根/容器任务）
     * //   一个父任务 = 一个 ADK App，用于 Trace 整个任务树的所有信息
     * String appName   = "mas-task-" + parentTask.getId();
     * // sessionId 对应叶子任务（具体执行单元）
     * String sessionId = task.getSessionId() + "_Task#" + task.getId();
     * String userId    = task.getCreatorId();
     * orchestratorV1.executeMacroLoop(appName, sessionId, userId, formattedGoal);
     * </pre>
     *
     * @param appName   父任务对应的 ADK App ID（全局唯一，支持整个任务树的 Trace/Resume）
     * @param sessionId 与 Leaf MasTask 一一对应的 ADK Session ID
     * @param userId    发起该 Task 的用户/租户 ID（ADK Session 归属）
     * @param goal      格式化后的任务目标（由 TaskContextFormatter 构建）
     * @return 执行结果 FINISH / SUSPEND / FAIL
     */
    public MasOrchestrationResult executeMacroLoop(String appName, String sessionId,
                                                    String userId, String goal) {
        log.info("[OrchestratorV1] Starting. appName={}, sessionId={}, userId={}", appName, sessionId, userId);

        // 1. 获取或恢复 ADK Session（SUSPEND 后重入时直接复用持久化 Session）
        Session session = getOrCreateSession(appName, sessionId, userId, goal);
        int phaseCount = getPhaseCount(session);

        while (phaseCount <= MAX_PHASES) {
            log.info("[OrchestratorV1] ======== BEGIN PHASE {} ========", phaseCount);
            publishOrchEvent(MasEventType.PHASE_STARTED, sessionId, phaseCount,
                    "Phase " + phaseCount + " started");

            // 2. Phase 边界：响应 URGENT 中断（非阻塞轮询）
            handleUrgentInterrupt(session, sessionId, phaseCount);

            // 3. Planner 推理（最多 3 次重试）
            DagGenerationPlan plan = invokePlannerWithRetry(session, sessionId, userId, phaseCount);
            if (plan == null) {
                // Planner 三次失败 → 交 Reviewer 定性
                return invokeReviewer(sessionId, "Planner failed after 3 retries in phase " + phaseCount);
            }

            // 4. 根据 Planner 返回状态决策
            switch (plan.getStatus()) {
                case DONE -> {
                    log.info("[OrchestratorV1] DONE. phase={}, reason={}", phaseCount, plan.getReasoning());
                    publishOrchEvent(MasEventType.TASK_FINISHED, sessionId, phaseCount,
                            "Task finished: " + plan.getReasoning());
                    return MasOrchestrationResult.builder()
                            .type(MasOrchestrationResult.ResultType.FINISH)
                            .output(plan.getReasoning())
                            .build();
                }
                case SUSPEND -> {
                    log.info("[OrchestratorV1] SUSPEND. phase={}", phaseCount);
                    publishOrchEvent(MasEventType.PHASE_SUSPENDED, sessionId, phaseCount,
                            "Phase suspended, awaiting user approval");
                    return MasOrchestrationResult.builder()
                            .type(MasOrchestrationResult.ResultType.SUSPEND)
                            .output(plan.getReasoning())
                            .build();
                }
                default -> {
                    // IN_PROGRESS：执行 DAG
                }
            }

            // 5. 组合 ADK Agent 图并执行
            BaseAgent phaseAgent = dagDispatcher.compose(plan, sessionId, phaseCount, eventPublisher);
            boolean ok = executePhaseAgent(phaseAgent, appName, sessionId, userId, phaseCount);
            if (!ok) {
                return invokeReviewer(sessionId, "Phase " + phaseCount + " timed out or failed");
            }

            publishOrchEvent(MasEventType.PHASE_COMPLETED, sessionId, phaseCount,
                    "Phase " + phaseCount + " completed");

            // 6. 推进 Phase 计数（写入 session.state，由 ADK SessionService 持久化）
            phaseCount++;
            writeStateKey(session, KEY_PHASE_COUNT, phaseCount);
        }

        log.warn("[OrchestratorV1] Reached max phase limit ({})", MAX_PHASES);
        return invokeReviewer(sessionId, "Reached maximum phase limit of " + MAX_PHASES);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Phase 门控：用户审批后恢复
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * 用户审批后恢复执行（Phase 门控场景 A）.
     * <p>向 session.state 写入审批结果，然后重新触发宏循环（从当前 phaseCount 继续）。
     *
     * @param appName      父任务对应的 ADK App ID（与 executeMacroLoop 一致）
     * @param sessionId    目标 Session ID
     * @param userId       用户 ID
     * @param goal         原始任务目标
     * @param userApproval 用户的审批内容
     */
    public MasOrchestrationResult resume(String appName, String sessionId, String userId,
                                         String goal, String userApproval) {
        log.info("[OrchestratorV1] Resuming. appName={}, sessionId={}, approval={}", appName, sessionId, userApproval);
        Session session = getOrCreateSession(appName, sessionId, userId, goal);
        writeStateKey(session, KEY_USER_APPROVAL, userApproval);
        return executeMacroLoop(appName, sessionId, userId, goal);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  内部辅助方法
    // ─────────────────────────────────────────────────────────────────────────

    private Session getOrCreateSession(String appName, String sessionId, String userId, String goal) {
        // TODO: 使用 ADK SessionService 正式实现，以下为 stub
        // return runner.sessionService()
        //     .createSession(appName, userId, Map.of(KEY_USER_GOAL, goal), sessionId)
        //     .blockingGet();
        throw new UnsupportedOperationException(
                "TODO: implement ADK SessionService.getOrCreate. appName=" + appName);
    }

    private int getPhaseCount(Session session) {
        Object v = session.state().get(KEY_PHASE_COUNT);
        return (v instanceof Number n) ? n.intValue() : 1;
    }

    private void handleUrgentInterrupt(Session session, String sessionId, int phaseIndex) {
        InterruptSignal signal = interruptManager.poll();
        if (signal == null || !signal.isUrgent()) return;

        log.info("[OrchestratorV1] URGENT interrupt at phase boundary: {}", signal.message());
        writeStateKey(session, KEY_URGENT_INTERRUPT, signal.message());
        eventPublisher.publish(MasEvent.builder()
                .type(MasEventType.INTERRUPT_TRIGGERED)
                .sessionId(sessionId)
                .phaseIndex(phaseIndex)
                .summary("Urgent interrupt received, Planner will re-plan next phase")
                .payload(signal.message())
                .build());
    }

    /**
     * 调用 Planner Agent，最多重试 3 次.
     * <p>
     * TODO: 当 PlannerAgent 完成 ADK 化后改为通过 {@code runner.runAsync()} 执行，
     * 并借助 session.state 读取历史上下文（替代现有的 NodeMemory 机制）。
     * plannerAgent 字段已就绪，未来直接通过 runner.runAsync(userId, sessionId, ...) 驱动。
     *
     * @return 解析后的 DagGenerationPlan，失败返回 null
     */
    @SuppressWarnings("unused") // plannerAgent & runner 在 TODO 完成后使用
    private DagGenerationPlan invokePlannerWithRetry(Session session, String sessionId,
                                                      String userId, int phaseIndex) {
        eventPublisher.publish(MasEvent.builder()
                .type(MasEventType.PLANNER_STARTED)
                .sessionId(sessionId)
                .phaseIndex(phaseIndex)
                .summary("Planner started reasoning for phase " + phaseIndex)
                .build());

        String planJson = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                // TODO: 替换为 ADK runner.runAsync() 调用 plannerAgent（LlmAgent）
                // 现阶段仍复用 plannerAgent.execute(localNodeMemory) 过渡实现
                planJson = "<TODO: invoke plannerAgent via runner>";
                log.debug("[OrchestratorV1] Planner attempt {}/3 succeeded", attempt);
                break;
            } catch (Exception e) {
                log.warn("[OrchestratorV1] Planner attempt {}/3 failed: {}", attempt, e.getMessage());
                if (attempt == 3) {
                    eventPublisher.publish(MasEvent.builder()
                            .type(MasEventType.PLANNER_FAILED)
                            .sessionId(sessionId)
                            .phaseIndex(phaseIndex)
                            .summary("Planner failed after 3 retries")
                            .payload(e.getMessage())
                            .build());
                    return null;
                }
            }
        }

        try {
            DagGenerationPlan plan = JsonUtils.parseObject(planJson, DagGenerationPlan.class);
            eventPublisher.publish(MasEvent.builder()
                    .type(MasEventType.PLANNER_COMPLETED)
                    .sessionId(sessionId)
                    .phaseIndex(phaseIndex)
                    .summary("Planner: " + plan.getStatus() + " — " + plan.getReasoning())
                    .payload(planJson)
                    .build());
            return plan;
        } catch (Exception e) {
            log.error("[OrchestratorV1] Failed to parse Planner JSON: {}", planJson, e);
            return null;
        }
    }

    /**
     * 执行由 {@link MasDagDispatcher} 组合的 ADK Agent 图，等待完成或超时.
     *
     * @return {@code true} 执行完成，{@code false} 超时/异常
     */
    private boolean executePhaseAgent(BaseAgent phaseAgent, String appName, String sessionId,
                                       String userId, int phaseIndex) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        // TODO: 替换为正式的 runner.runAsync() 调用
        // Content phaseInput = Content.fromParts(Part.fromText("Execute DAG for phase " + phaseIndex));
        // Flowable<Event> stream = runner.runAsync(userId, sessionId, phaseInput);
        // Disposable sub = stream.subscribe(
        //     event -> { /* Callbacks 已处理事件，此处无需额外处理 */ },
        //     err   -> { error.set(err); latch.countDown(); },
        //     ()    -> latch.countDown()
        // );

        // Stub：直接放行（实际替换后删除）
        latch.countDown();

        try {
            boolean completed = latch.await(PHASE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            if (!completed) {
                log.warn("[OrchestratorV1] Phase {} timed out after {} minutes", 
                         phaseIndex, PHASE_TIMEOUT_MINUTES);
            }
            if (error.get() != null) {
                log.error("[OrchestratorV1] Phase {} failed with error", phaseIndex, error.get());
                return false;
            }
            return completed;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[OrchestratorV1] Phase {} interrupted", phaseIndex);
            return false;
        }
    }

    /**
     * 调用 ReviewerAgent 进行最终裁决.
     * <p>
     * TODO: ReviewerAgent 也需完成 ADK 化，改为通过 runner.runAsync() 执行。
     * reviewerAgent 字段已就绪。
     */
    private MasOrchestrationResult invokeReviewer(String sessionId, String issue) {
        log.info("[OrchestratorV1] Invoking ReviewerAgent. issue={}", issue);
        // TODO: return reviewerAgent.review(localNodeMemory, issue);
        eventPublisher.publish(MasEvent.builder()
                .type(MasEventType.TASK_FAILED)
                .sessionId(sessionId)
                .phaseIndex(0)
                .summary("Task failed: " + issue)
                .build());
        return MasOrchestrationResult.builder()
                .type(MasOrchestrationResult.ResultType.FAIL)
                .errorMessage(issue)
                .build();
    }

    private void publishOrchEvent(MasEventType type, String sessionId, int phaseIndex, String summary) {
        eventPublisher.publish(MasEvent.builder()
                .type(type).sessionId(sessionId).phaseIndex(phaseIndex).summary(summary).build());
    }

    /**
     * 写入单个 session.state key.
     * <p>
     * TODO: 替换为通过 {@code EventActions.stateDelta} + {@code sessionService.appendEvent()}
     * 的标准方式，确保事件追踪和持久化。
     */
    private void writeStateKey(Session session, String key, Object value) {
        session.state().put(key, value);
        log.debug("[OrchestratorV1] state[{}] = {}", key, value);
    }
}
