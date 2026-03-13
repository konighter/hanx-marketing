package com.hzltd.module.erplus.adv.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Schema(description = "策略指导书结构化解析结果 VO")
@Data
public class StrategyInstructionVO {

    @Schema(description = "策略概览图文摘要体")
    private Summary summary;

    @Schema(description = "策略运行原版 Markdown 文档")
    private String strategy;

    @Data
    public static class Summary {
        @Schema(description = "总体描述，例如原理性和目标简介")
        private String description;

        @Schema(description = "各阶段 (Phase) 的抽象阶段列表")
        private List<Phase> phases;
    }

    @Data
    public static class Phase {
        @Schema(description = "阶段名称，例如 Phase 1: 冷启动期")
        private String name;

        @Schema(description = "阶段执行目标、原理简述")
        private String description;
    }
}
