package com.hzltd.module.erplus.ai.mas.framework.execution;

import com.hzltd.module.erplus.ai.mas.framework.agent.MasAgent;
import reactor.core.publisher.Mono;

/**
 * MAS 任务执行器接口 (支持本地/分布式扩展)
 */
public interface MasTaskExecutor {

    /**
     * 执行具体任务
     * @param task 任务定义
     * @param agent 选定的执行智能体
     * @param context 执行上下文
     * @return 执行结果
     */
    Mono<String> execute(MasTask task, MasAgent agent, MasContext context);
}
