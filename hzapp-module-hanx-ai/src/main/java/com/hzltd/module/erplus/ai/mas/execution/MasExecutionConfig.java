package com.hzltd.module.erplus.ai.mas.execution;


import com.hzltd.module.erplus.ai.mas.spi.execution.NodeRunner;
import com.hzltd.module.erplus.ai.mas.spi.execution.adk.AdkNodeRunner;
import com.hzltd.module.erplus.ai.mas.spi.execution.react.ReActLlmClient;
import com.hzltd.module.erplus.ai.mas.spi.execution.react.ReActNodeRunner;
import com.hzltd.module.erplus.ai.mas.tools.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring Configuration for MAS runtime execution infrastructure.
 * Centralises thread pool and NodeRunner bean creation.
 */
@Slf4j
@Configuration
public class MasExecutionConfig {

    /**
     * Thread pool used by {@link DagExecutionEngine} to dispatch node execution.
     */
    @Bean(name = "masNodeExecutorPool", destroyMethod = "shutdown")
    public ExecutorService masNodeExecutorPool() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "mas-node-executor-" + counter.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        };

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                8,   // core pool size
                32,  // max pool size
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(128),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        pool.allowCoreThreadTimeOut(true);

        log.info("[MasExecutionConfig] Initialised masNodeExecutorPool: core=8, max=32, queue=128");
        return pool;
    }

    /**
     * Default NodeRunner — delegates to agent.execute() for SIMPLE nodes.
     */
    @Bean
    @Primary
    public NodeRunner defaultNodeRunner() {
        return new AdkNodeRunner();
    }

    /**
     * ReAct NodeRunner — Think→Act→Observe loop for REACT nodes.
     * <p>
     * Requires a {@link ReActLlmClient} bean to be provided by the application.
     * If no ReActLlmClient is available, this bean is not created.
     */
    @Bean
    @ConditionalOnMissingBean(name = "reactNodeRunner")
    public NodeRunner reactNodeRunner(@Autowired(required = false) ReActLlmClient llmClient,
                                      ToolRegistry toolRegistry,
                                      ExecutionProgressReporter progressReporter) {
        if (llmClient == null) {
            log.warn("[MasExecutionConfig] No ReActLlmClient bean found — ReActNodeRunner will NOT be available. " +
                     "REACT nodes will fall back to default runner.");
            return null;
        }
        log.info("[MasExecutionConfig] Initialised ReActNodeRunner with max 25 steps");
        return new ReActNodeRunner(toolRegistry, progressReporter, llmClient, 25);
    }
}
