package com.hzltd.module.erplus.adv.mas.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "策略指导书结构化解析结果 VO")
@Data
public class StrategyInstructionVO {

    @Schema(description = "策略概览图文摘要体")
    private Summary summary;

    @Schema(description = "策略运行原版 Markdown 文档")
    private List<PhaseDefinition> strategy;

    @Data
    public static class Summary {
        @Schema(description = "总体描述，例如原理性和目标简介")
        private String description;

        @Schema(description = "各阶段 (Phase) 的抽象阶段列表")
        private List<PhaseSummary> phases;
    }

    @Data
    public static class PhaseSummary {
        @Schema(description = "阶段名称，例如 Phase 1: 冷启动期")
        private String name;

        @Schema(description = "阶段执行目标、原理简述")
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhaseDefinition extends PhaseSummary {


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
         * Max iteration count per phase (default: 5).
         * Controls how many collect-decide-execute-review cycles to run.
         */
        private Integer maxIterations;

        /**
         * Required tools for this phase.
         */
        private List<String> tools;

        /**
         * Strategy implementation instruction.
         */
        private String instruction;
    }
}
