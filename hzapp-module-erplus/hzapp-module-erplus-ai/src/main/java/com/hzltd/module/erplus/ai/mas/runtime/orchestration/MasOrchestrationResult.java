package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Orchestrator 执行结果 (与业务 Task 状态解耦)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasOrchestrationResult {

    /**
     * 执行结果类型
     */
    public enum ResultType {
        /**
         * 任务已达成目标
         */
        FINISH,
        /**
         * 出现不可恢复错误
         */
        FAIL,
        /**
         * 任务需要挂起，等待稍后恢复执行
         */
        SUSPEND,
        /**
         * 继续内部循环 (默认状态)
         */
        CONTINUE
    }

    /**
     * 结果类型
     */
    private ResultType type;

    /**
     * 下次建议执行时间 (仅 type 为 SUSPEND 时有效)
     */
    private LocalDateTime nextExecuteTime;

    /**
     * 执行过程的历史/总结数据
     */
    private String history;

    /**
     * 最终输出 (仅 type 为 FINISH 时有意义)
     */
    private String output;

    /**
     * 错误详情 (如果有)
     */
    private String errorMessage;
}
