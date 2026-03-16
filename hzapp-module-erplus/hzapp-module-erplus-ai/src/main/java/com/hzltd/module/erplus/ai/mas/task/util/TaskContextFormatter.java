package com.hzltd.module.erplus.ai.mas.task.util;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;

/**
 * Utility to format the high-level goal and context for a MAS Task.
 * Combines Strategy Instruction, Input Data, and Retry information.
 */
public class TaskContextFormatter {

    /**
     * Formats the task context into a single string for LLM consumption.
     * 
     * @param task The task to format
     * @return Formatted context string
     */
    public static String formatTaskGoal(MasTaskDO task) {
        StringBuilder sb = new StringBuilder();
        
        // 1. Core Goal / Instruction
        sb.append("### Task Name: ").append(task.getName()).append("\n\n");
        
        // 2. Strategy / Strategy Instruction
        if (task.getStrategyInstruction() != null && !task.getStrategyInstruction().isEmpty()) {
            sb.append("### Strategy Instruction:\n")
              .append(task.getStrategyInstruction()).append("\n\n");
        }
        
        // 3. Input Data
        if (task.getInputData() != null && !task.getInputData().isEmpty()) {
            sb.append("### Input Data:\n")
              .append(task.getInputData()).append("\n\n");
        }
        
        // 4. Retry / Error Context
        if (task.getRetryData() != null && !task.getRetryData().isEmpty()) {
            sb.append("### Previous Execution Context / Retry Info:\n")
              .append(task.getRetryData()).append("\n\n");
        }
        
        return sb.toString().trim();
    }
}
