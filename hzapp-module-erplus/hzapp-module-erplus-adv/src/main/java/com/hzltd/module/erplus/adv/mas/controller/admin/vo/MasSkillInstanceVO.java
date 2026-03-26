package com.hzltd.module.erplus.adv.mas.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - MAS 技能实例 VO")
public class MasSkillInstanceVO {

    @Schema(description = "实例 ID", example = "1024")
    private Long id;

    @Schema(description = "Skill 代码", example = "AD_MONITOR")
    private String skillCode;

    /**
     * 工作流配置 (JSON)
     */
    @Schema(description = "工作流配置 (JSON)")
    private String workflowConfig;

    /**
     * 会话 ID
     */
    @Schema(description = "会话 ID")
    private String sessionId;

    @Schema(description = "流程实例 ID", example = "proc_123")
    private String processInstanceId;

    @Schema(description = "Skill 名称", example = "广告监控")
    private String skillName;

    @Schema(description = "目标业务 ID (ASIN)", example = "B00XXXXX")
    private String targetBizId;

    @Schema(description = "运行状态", example = "运行中")
    private String status;

    @Schema(description = "当前阶段/步骤", example = "数据分析")
    private String currentStage;

    @Schema(description = "最新进展", example = "已发现 3 个高转化关键词")
    private String latestProgress;

    @Schema(description = "激活时间")
    private LocalDateTime createTime;

}
