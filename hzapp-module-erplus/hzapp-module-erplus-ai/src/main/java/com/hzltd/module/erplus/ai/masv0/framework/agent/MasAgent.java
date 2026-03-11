package com.hzltd.module.erplus.ai.masv0.framework.agent;

import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasTask;
import reactor.core.publisher.Mono;

/**
 * MAS Agent 基础接口
 */
public interface MasAgent {

    /**
     * 获取 Agent 角色
     */
    MasRole getRole();

    /**
     * 获取 Agent 名称
     */
    String getName();

    /**
     * 在给定上下文中处理任务
     */
    Mono<String> handle(MasTask task, MasContext context);
}
