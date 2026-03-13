package com.hzltd.module.erplus.ai.mas.runtime.task;

import com.hzltd.module.erplus.ai.mas.runtime.agent.PlannerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.agent.ReviewerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.execution.NodeExecutorFactory;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.DagParserUtil;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.WorkflowOrchestrator;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * Configuration for MAS Orchestration components.
 * Enables modular initialization of {@link WorkflowOrchestrator}.
 */
@Slf4j
@Configuration
public class MasOrchestrationConfig {

    @Bean
    public WorkflowOrchestrator workflowOrchestrator(
            PlannerAgent plannerAgent,
            ReviewerAgent reviewerAgent,
            DagParserUtil dagParserUtil,
            MasMemoryManager memoryManager,
            MasCheckpointService checkpointService,
            MasEventLogService eventLogService,
            @Qualifier("masNodeExecutorPool") ExecutorService masNodeExecutorPool,
            NodeExecutorFactory nodeExecutorFactory) {
        
        log.info("[MasOrchestrationConfig] Initialising modular WorkflowOrchestrator");
        return new WorkflowOrchestrator(
                plannerAgent,
                reviewerAgent,
                dagParserUtil,
                memoryManager,
                checkpointService,
                eventLogService,
                masNodeExecutorPool,
                nodeExecutorFactory
        );
    }
}
