package com.hzltd.module.erplus.ai.mas.execution;

import com.hzltd.module.erplus.ai.mas.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.persistence.MasCheckpointService;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasSessionMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory to create NodeExecutor instances with all necessary services injected.
 * Decouples the creation of transient executors from the infrastructure services.
 */
@Component
public class NodeExecutorFactory {

    private final MasCheckpointService checkpointService;
    private final MasMemoryManager memoryManager;
    private final MasEventLogService eventLogService;
    private final NodeRunnerProvider runnerProvider;

    @Autowired
    public NodeExecutorFactory(MasCheckpointService checkpointService,
                               MasMemoryManager memoryManager,
                               MasEventLogService eventLogService,
                               NodeRunnerProvider runnerProvider) {
        this.checkpointService = checkpointService;
        this.memoryManager = memoryManager;
        this.eventLogService = eventLogService;
        this.runnerProvider = runnerProvider;
    }

    /**
     * Creates a new NodeExecutor for the given node and session.
     */
    public NodeExecutor create(GraphNode node, MasSessionMemory sessionMemory) {
        return new NodeExecutor(
                node,
                sessionMemory,
                checkpointService,
                memoryManager,
                eventLogService,
                runnerProvider
        );
    }
}
