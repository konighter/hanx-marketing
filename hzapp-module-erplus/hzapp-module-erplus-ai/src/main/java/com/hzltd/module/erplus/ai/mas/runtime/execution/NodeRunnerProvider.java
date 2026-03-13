package com.hzltd.module.erplus.ai.mas.runtime.execution;

import com.hzltd.module.erplus.ai.mas.runtime.spi.execution.NodeRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Provider to resolve the appropriate NodeRunner based on node type.
 * Centralizes the logic of routing between default and ReAct runners.
 */
@Component
public class NodeRunnerProvider {

    private final NodeRunner defaultNodeRunner;
    private final NodeRunner reactNodeRunner;

    @Autowired
    public NodeRunnerProvider(NodeRunner defaultNodeRunner,
                               @Autowired(required = false) @Qualifier("reactNodeRunner") NodeRunner reactNodeRunner) {
        this.defaultNodeRunner = defaultNodeRunner;
        this.reactNodeRunner = reactNodeRunner;
    }

    /**
     * Returns the runner appropriate for the given node.
     */
    public NodeRunner getRunner(GraphNode.NodeType type) {
        if (type == GraphNode.NodeType.REACT && reactNodeRunner != null) {
            return reactNodeRunner;
        }
        return defaultNodeRunner;
    }
}
