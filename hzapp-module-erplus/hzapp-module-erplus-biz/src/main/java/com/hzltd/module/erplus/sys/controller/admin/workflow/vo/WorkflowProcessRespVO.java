package com.hzltd.module.erplus.sys.controller.admin.workflow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 流程定义 Response VO")
public class WorkflowProcessRespVO {

    @Schema(description = "流程定义ID", example = "process:1:12345")
    private String id;

    @Schema(description = "流程名称", example = "测试流程")
    private String name;

    @Schema(description = "流程标识", example = "testProcess")
    private String key;

    @Schema(description = "版本", example = "1")
    private Integer version;

    @Schema(description = "部署ID", example = "10001")
    private String deploymentId;

    @Schema(description = "资源名称", example = "test.bpmn20.xml")
    private String resourceName;

    @Schema(description = "状态：1-激活，2-挂起", example = "1")
    private Integer suspensionState;

}
