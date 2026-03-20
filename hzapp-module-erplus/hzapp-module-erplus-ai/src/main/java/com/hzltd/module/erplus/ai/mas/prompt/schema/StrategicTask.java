package com.hzltd.module.erplus.ai.mas.prompt.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a high-level business task within a StrategicPlan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategicTask {
    /**
     * Unique name or identifier for this task stage.
     */
    private String name;

    /**
     * Task type (SEQUENTIAL, PARALLEL, LEAF).
     */
    private String type;

    /**
     * High-level goal or instruction for this specific task.
     */
    private String goal;

    /**
     * Order of execution for sequential tasks.
     */
    private Integer executionOrder;

    /**
     * List of task names this task depends on.
     */
    private List<String> dependsOn;
}
