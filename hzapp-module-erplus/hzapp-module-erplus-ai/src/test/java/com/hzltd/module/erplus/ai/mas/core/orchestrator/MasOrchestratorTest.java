package com.hzltd.module.erplus.ai.mas.core.orchestrator;

import com.hzltd.module.erplus.ai.mas.framework.agent.MasAgent;
import com.hzltd.module.erplus.ai.mas.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.mas.framework.event.DefaultMasEventBus;
import com.hzltd.module.erplus.ai.mas.framework.event.MasEventBus;
import com.hzltd.module.erplus.ai.mas.framework.execution.LocalMasTaskExecutor;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasTask;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasTaskExecutor;
import com.hzltd.module.erplus.ai.mas.framework.state.MasState;
import com.hzltd.module.erplus.ai.service.mas.MasPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;

public class MasOrchestratorTest {

    private MasEventBus eventBus;
    private MasTaskExecutor taskExecutor;
    private MasPersistenceService persistenceService;
    private MasOrchestrator orchestrator;
    private MasAgent managerAgent;

    @BeforeEach
    void setUp() {
        eventBus = new DefaultMasEventBus();
        taskExecutor = Mockito.spy(new LocalMasTaskExecutor(eventBus));
        persistenceService = Mockito.mock(MasPersistenceService.class);
        orchestrator = new MasOrchestrator("test-session", eventBus, taskExecutor, persistenceService);

        managerAgent = Mockito.mock(MasAgent.class);
        when(managerAgent.getRole()).thenReturn(MasRole.MANAGER);
        when(managerAgent.getName()).thenReturn("MockManager");
        
        // 模拟 Manager 的初始化决策
        when(managerAgent.handle(any(MasTask.class), any(MasContext.class)))
                .thenReturn(Mono.just("{\"action\": \"INIT_SUCCESS\", \"next_step\": \"PLANNING\"}"));

        orchestrator.registerAgent(managerAgent);
    }

    @Test
    void testStartSessionAndInitialTransition() {
        // 执行启动
        Mono<Void> result = orchestrator.start("设计一个营销方案");

        // 验证流程：状态应从 INIT 变为 PLANNING
        StepVerifier.create(result)
                .verifyComplete();

        // 验证持久化是否被调用 (recordTaskHistory 由事件驱动异步触发，给一个超时窗口)
        Mockito.verify(persistenceService, timeout(1000).atLeastOnce())
                .recordTaskHistory(anyString(), any(MasTask.class), anyString(), anyString(), anyLong());
    }
}
