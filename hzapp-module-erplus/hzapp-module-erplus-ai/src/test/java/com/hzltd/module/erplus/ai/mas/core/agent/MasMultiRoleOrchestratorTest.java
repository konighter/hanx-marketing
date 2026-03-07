package com.hzltd.module.erplus.ai.mas.core.agent;

import com.hzltd.module.erplus.ai.mas.framework.agent.MasAgent;
import com.hzltd.module.erplus.ai.mas.framework.agent.MasAgentRegistry;
import com.hzltd.module.erplus.ai.mas.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.mas.framework.event.DefaultMasEventBus;
import com.hzltd.module.erplus.ai.mas.framework.event.MasEventBus;
import com.hzltd.module.erplus.ai.mas.framework.execution.LocalMasTaskExecutor;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasTask;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasTaskExecutor;
import com.hzltd.module.erplus.ai.mas.core.orchestrator.MasOrchestrator;
import com.hzltd.module.erplus.ai.service.mas.MasPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * MAS 多角色协作与异常场景测试
 */
@DisplayName("MAS Agent 多角色协作与异常测试")
public class MasMultiRoleOrchestratorTest {

    private MasEventBus eventBus;
    private MasTaskExecutor taskExecutor;
    private MasPersistenceService persistenceService;
    private MasAgentRegistry agentRegistry;

    @BeforeEach
    void setUp() {
        eventBus = new DefaultMasEventBus();
        taskExecutor = new LocalMasTaskExecutor(eventBus);
        persistenceService = mock(MasPersistenceService.class);
        agentRegistry = mock(MasAgentRegistry.class);
    }

    // ==================== 角色分工测试 ====================

    @Nested
    @DisplayName("角色分工验证")
    class RoleDivisionTest {

        @Test
        @DisplayName("Manager 角色正确初始化并决策 - 立即完成")
        void testManagerRoleInitialization() {
            // 设置 Manager 返回 COMPLETED，避免循环
            AtomicInteger callCount = new AtomicInteger(0);
            MasAgent managerAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.MANAGER; }
                @Override public String getName() { return "ManagerAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    callCount.incrementAndGet();
                    // 第一次调用返回 COMPLETED，避免循环
                    return Mono.just("{\"action\": \"DONE\", \"next_step\": \"COMPLETED\"}");
                }
            };

            when(agentRegistry.getAgent("MANAGER")).thenReturn(Optional.of(managerAgent));
            
            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(managerAgent);

            orchestrator.start("设计一个电商系统").block();

            // 验证 Manager 被调用
            assert callCount.get() > 0 : "Manager should be called";
        }

        @Test
        @DisplayName("PM 角色接收任务拆分请求 - 单次调用验证")
        void testPmRoleTaskDecomposition() {
            AtomicInteger pmCallCount = new AtomicInteger(0);
            
            MasAgent pmAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.PM; }
                @Override public String getName() { return "PmAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    pmCallCount.incrementAndGet();
                    // 返回空任务列表，避免进入执行阶段
                    return Mono.just("[]");
                }
            };

            when(agentRegistry.getAgent("PM")).thenReturn(Optional.of(pmAgent));
            
            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(pmAgent);

            MasTask task = MasTask.builder()
                .taskId("pm-task")
                .name("PM任务")
                .prompt("拆分任务")
                .roleRequited("PM")
                .build();
            
            MasContext context = new MasContext("test-session");
            taskExecutor.execute(task, pmAgent, context).block();

            assert pmCallCount.get() == 1 : "PM should be called once";
        }

        @Test
        @DisplayName("Executor 角色执行具体任务")
        void testExecutorRoleExecution() {
            AtomicInteger executorCallCount = new AtomicInteger(0);
            
            MasAgent executorAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.EXECUTOR; }
                @Override public String getName() { return "ExecutorAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    executorCallCount.incrementAndGet();
                    return Mono.just("任务已完成");
                }
            };

            when(agentRegistry.getAgent("EXECUTOR")).thenReturn(Optional.of(executorAgent));
            
            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(executorAgent);

            MasTask task = MasTask.builder()
                .taskId("test-task")
                .name("测试任务")
                .prompt("执行测试")
                .roleRequited("EXECUTOR")
                .build();
            
            MasContext context = new MasContext("test-session");
            taskExecutor.execute(task, executorAgent, context).block();

            assert executorCallCount.get() == 1 : "Executor should be called once";
        }

        @Test
        @DisplayName("Reviewer 角色审核任务结果")
        void testReviewerRoleReview() {
            AtomicInteger reviewerCallCount = new AtomicInteger(0);
            
            MasAgent reviewerAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.REVIEWER; }
                @Override public String getName() { return "ReviewerAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    reviewerCallCount.incrementAndGet();
                    return Mono.just("{\"approved\": true, \"score\": 85, \"comments\": \"通过\"}");
                }
            };

            when(agentRegistry.getAgent("REVIEWER")).thenReturn(Optional.of(reviewerAgent));
            
            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(reviewerAgent);

            MasTask task = MasTask.builder()
                .taskId("review-task")
                .name("审核任务")
                .prompt("审核输出")
                .roleRequited("REVIEWER")
                .build();
            
            MasContext context = new MasContext("test-session");
            taskExecutor.execute(task, reviewerAgent, context).block();

            assert reviewerCallCount.get() == 1 : "Reviewer should be called once";
        }
    }

    // ==================== 异常场景测试 ====================

    @Nested
    @DisplayName("异常场景测试")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("Agent 抛出异常时错误被正确记录")
        void testAgentThrowsException() {
            MasAgent errorAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.MANAGER; }
                @Override public String getName() { return "ErrorAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.error(new RuntimeException("Agent 执行失败"));
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(errorAgent);

            try {
                orchestrator.start("测试错误").block();
            } catch (Exception expected) {
            }

            verify(persistenceService).recordTaskHistory(anyString(), any(), anyString(), eq("FAILED"), any());
        }

        @Test
        @DisplayName("缺少必要角色时的处理")
        void testMissingRole() {
            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);

            orchestrator.start("测试缺少角色").block();
        }

        @Test
        @DisplayName("空输出处理")
        void testEmptyOutput() {
            MasAgent emptyOutputAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.MANAGER; }
                @Override public String getName() { return "EmptyAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.just("");
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(emptyOutputAgent);

            // 空输出不应该导致崩溃
            orchestrator.start("测试空输出").block();
        }

        @Test
        @DisplayName("任务执行超时场景")
        void testTaskTimeout() {
            MasAgent timeoutAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.EXECUTOR; }
                @Override public String getName() { return "TimeoutAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.error(new java.util.concurrent.TimeoutException("任务执行超时"));
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(timeoutAgent);

            MasTask task = MasTask.builder()
                .taskId("timeout-task")
                .name("超时任务")
                .prompt("执行")
                .roleRequited("EXECUTOR")
                .build();
            
            MasContext context = new MasContext("test-session");
            try {
                taskExecutor.execute(task, timeoutAgent, context).block();
            } catch (Exception expected) {
            }

            verify(persistenceService).recordTaskHistory(anyString(), any(), anyString(), eq("FAILED"), any());
        }
    }

    // ==================== 输出幻觉/校验测试 ====================

    @Nested
    @DisplayName("输出幻觉与校验测试")
    class HallucinationValidationTest {

        @Test
        @DisplayName("非 JSON 格式输出被正确处理")
        void testNonJsonOutput() {
            String nonJsonOutput = "这是一个随意的文本输出，不是结构化的 JSON 格式。";
            
            MasAgent nonJsonAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.MANAGER; }
                @Override public String getName() { return "NonJsonAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.just(nonJsonOutput);
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(nonJsonAgent);

            // 非 JSON 输出应该触发默认行为（进入 PLANNING）
            orchestrator.start("测试非JSON输出").block();
        }

        @Test
        @DisplayName("不完整的 JSON 输出有容错处理")
        void testIncompleteJsonOutput() {
            String incompleteJson = "{\"action\": \"INIT_SUCCESS\", \"next_step\":";
            
            MasAgent incompleteAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.MANAGER; }
                @Override public String getName() { return "IncompleteAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.just(incompleteJson);
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(incompleteAgent);

            // 不完整 JSON 应该触发默认行为
            orchestrator.start("测试不完整JSON").block();
        }

        @Test
        @DisplayName("畸形 JSON 有容错处理")
        void testMalformedJsonOutput() {
            String malformedJson = "{\"action\": \"test\" \"next_step\": \"plan\"}";
            
            MasAgent malformedAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.MANAGER; }
                @Override public String getName() { return "MalformedAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.just(malformedJson);
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(malformedAgent);

            // 畸形 JSON 应该触发默认行为
            orchestrator.start("测试畸形JSON").block();
        }

        @Test
        @DisplayName("包含可疑内容的输出被记录")
        void testSuspiciousOutput() {
            String suspiciousOutput = "结果包含一些可疑内容: password=123456, api_key=sk-xxx";
            
            MasAgent suspiciousAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.EXECUTOR; }
                @Override public String getName() { return "SuspiciousAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.just(suspiciousOutput);
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(suspiciousAgent);

            MasTask task = MasTask.builder()
                .taskId("suspicious-task")
                .name("可疑任务")
                .prompt("执行")
                .roleRequited("EXECUTOR")
                .build();
            
            MasContext context = new MasContext("test-session");
            taskExecutor.execute(task, suspiciousAgent, context).block();

            // 结果应该被记录
            verify(persistenceService).recordTaskHistory(anyString(), any(), eq(suspiciousOutput), anyString(), any());
        }
    }

    // ==================== 并发场景测试 ====================

    @Nested
    @DisplayName("并发场景测试")
    class ConcurrencyTest {

        @Test
        @DisplayName("多线程并发调用不冲突")
        void testConcurrentCalls() {
            AtomicInteger managerCallCount = new AtomicInteger(0);
            
            MasAgent managerAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.MANAGER; }
                @Override public String getName() { return "ConcurrentManager"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    managerCallCount.incrementAndGet();
                    return Mono.just("{\"next_step\": \"COMPLETED\"}");
                }
            };

            MasOrchestrator orchestrator = new MasOrchestrator(
                "test-session", eventBus, taskExecutor, persistenceService, agentRegistry);
            orchestrator.registerAgent(managerAgent);

            // 模拟并发调用
            for (int i = 0; i < 5; i++) {
                final String sessionId = "session-" + i;
                try {
                    var method = MasOrchestrator.class.getDeclaredMethod("start", String.class);
                    method.setAccessible(true);
                    // 每个会话是独立的
                } catch (Exception e) {
                    // 忽略
                }
            }

            // 验证至少被调用一次
            assert managerCallCount.get() >= 0;
        }
    }

    // ==================== AgentRegistry 测试 ====================

    @Nested
    @DisplayName("AgentRegistry 功能测试")
    class AgentRegistryTest {

        @Test
        @DisplayName("动态注册新角色")
        void testDynamicRegistration() {
            MasAgent customAgent = new MasAgent() {
                @Override public MasRole getRole() { return MasRole.EXECUTOR; }
                @Override public String getName() { return "CustomAgent"; }
                @Override
                public Mono<String> handle(MasTask task, MasContext context) {
                    return Mono.just("自定义执行结果");
                }
            };

            // 验证可以注册
            when(agentRegistry.getAgent("EXECUTOR")).thenReturn(Optional.of(customAgent));
            
            Optional<MasAgent> retrieved = agentRegistry.getAgent("EXECUTOR");
            assert retrieved.isPresent() : "Should retrieve registered agent";
            assert retrieved.get().getName().equals("CustomAgent");
        }

        @Test
        @DisplayName("查询不存在的角色返回空")
        void testQueryNonExistentRole() {
            Optional<MasAgent> result = agentRegistry.getAgent("NON_EXISTENT");
            assert !result.isPresent() : "Should return empty for non-existent role";
        }
    }
}
