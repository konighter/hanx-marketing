package com.hzltd.module.erplus.ai.masv0.framework.execution;

import com.hzltd.module.erplus.ai.masv0.framework.agent.MasAgent;
import com.hzltd.module.erplus.ai.masv0.framework.event.MasEventBus;
import com.hzltd.module.erplus.ai.masv0.framework.event.TaskFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 本地任务执行器实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalMasTaskExecutor implements MasTaskExecutor {

    private final MasEventBus eventBus;

    @Override
    public Mono<String> execute(MasTask task, MasAgent agent, MasContext context) {
        log.info("[Execution] 开始执行任务: {} -> 由 Agent {} 执行", task.getName(), agent.getName());
        long startTime = System.currentTimeMillis();
        
        return agent.handle(task, context)
                .doOnSuccess(result -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    log.info("[Execution] 任务执行成功: {}, 耗时: {}ms", task.getName(), executionTime);
                    eventBus.publish(new TaskFinishedEvent(context.getSessionId(), task, result, TaskFinishedEvent.Status.SUCCESS, executionTime));
                })
                .doOnError(e -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    log.error("[Execution] 任务执行失败: {}, 耗时: {}ms", task.getName(), executionTime, e);
                    eventBus.publish(new TaskFinishedEvent(context.getSessionId(), task, e.getMessage(), TaskFinishedEvent.Status.FAILED, executionTime));
                });
    }
}
