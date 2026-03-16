package com.hzltd.module.erplus.ai.mas.prompt.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the structured strategy instruction for a MAS task.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasTaskStrategy {

    /**
     * List of execution phases (the 'strategy' field).
     */
    private List<PhaseDefinition> strategy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhaseDefinition {
        /**
         * Phase name.
         */
        private String name;

        /**
         * Execution order.
         */
        private Integer order;

        /**
         * Execution cycle (e.g., ONCE, PERIODIC, CRON).
         */
        private String cycleType;

        /**
         * Execution interval in seconds.
         */
        private Integer interval;

        /**
         * Required tools for this phase.
         */
        private List<String> tools;

        /**
         * Strategy implementation description.
         */
        private String description;
    }
}
