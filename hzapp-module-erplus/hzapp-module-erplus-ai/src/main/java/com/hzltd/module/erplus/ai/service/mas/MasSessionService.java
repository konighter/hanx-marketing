package com.hzltd.module.erplus.ai.service.mas;

import com.hzltd.module.erplus.ai.mas.core.agent.ExecutorAgent;
import com.hzltd.module.erplus.ai.mas.core.agent.ManagerAgent;
import com.hzltd.module.erplus.ai.mas.core.agent.PmAgent;
import com.hzltd.module.erplus.ai.mas.core.orchestrator.MasOrchestrator;
import com.hzltd.module.erplus.ai.mas.framework.event.MasEventBus;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasTaskExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.hzltd.module.erplus.ai.mas.framework.state.MasState;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MAS 会话管理服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasSessionService {

    private final MasEventBus eventBus;
    private final MasTaskExecutor taskExecutor;
    private final MasPersistenceService persistenceService;
    
    // Agent Beans
    private final ManagerAgent managerAgent;
    private final PmAgent pmAgent;
    private final ExecutorAgent executorAgent;

    // 活跃会话池
    private final Map<String, MasOrchestrator> activeSessions = new ConcurrentHashMap<>();

    /**
     * 创建并启动一个新会话
     */
    public Mono<String> createSession(String goal) {
        String sessionId = UUID.randomUUID().toString();
        
        // 先持久化会话记录到数据库，确保前端可以立即查询到
        persistenceService.saveOrUpdateSession(sessionId, goal, MasState.INIT);
        
        MasOrchestrator orchestrator = new MasOrchestrator(sessionId, eventBus, taskExecutor, persistenceService);
        
        // 注册默认智能体角色 (后期可改为根据配置动态加载)
        orchestrator.registerAgent(managerAgent);
        orchestrator.registerAgent(pmAgent);
        orchestrator.registerAgent(executorAgent);
        
        activeSessions.put(sessionId, orchestrator);
        
        // 异步启动编排，不阻塞返回，将其提交到弹性的后台调度线程中
        orchestrator.start(goal)
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .doOnError(e -> log.error("[MasSessionService] 会话 {} 启动失败", sessionId, e))
                .subscribe();
        
        return Mono.just(sessionId);
    }

    /**
     * 获取活跃会话
     */
    public MasOrchestrator getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    /**
     * 手动终止会话
     */
    public Mono<Void> stopSession(String sessionId) {
        MasOrchestrator orchestrator = activeSessions.remove(sessionId);
        if (orchestrator != null) {
            return orchestrator.stop();
        } else {
            // 如果内存中没有，直接更新数据库状态
            persistenceService.saveOrUpdateSession(sessionId, "", MasState.FAILED);
            return Mono.empty();
        }
    }
}
