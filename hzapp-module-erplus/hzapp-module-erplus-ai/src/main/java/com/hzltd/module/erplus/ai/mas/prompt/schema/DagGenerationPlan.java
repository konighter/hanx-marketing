package com.hzltd.module.erplus.ai.mas.prompt.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DagGenerationPlan {
    
    public enum Status {
        /**
         * The overall task is still in progress, and the generated nodes should be executed.
         */
        IN_PROGRESS,
        
        /**
         * The overall task has been successfully achieved. No new nodes need to be executed.
         */
        DONE,
        
        /**
         * The task cannot be completed due to unresolvable errors or missing information.
         */
        FAILED,

        /**
         * The task needs to be paused and resumed later.
         */
        SUSPEND
    }
    
    /**
     * The macro-status of the overarching workflow.
     */
    private Status status;
    
    /**
     * The list of nodes to execute in the next phase. Empty if status is DONE or FAILED.
     */
    private List<DagPlanNode> nodes;
    
    /**
     * Manager's reasoning or summary of the current state.
     */
    private String reasoning;

    /**
     * Optional: Next execution time in ISO format if status is SUSPEND.
     */
    private String nextExecuteAt;
}
