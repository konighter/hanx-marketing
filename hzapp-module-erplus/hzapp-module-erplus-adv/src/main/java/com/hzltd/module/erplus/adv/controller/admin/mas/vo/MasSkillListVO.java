package com.hzltd.module.erplus.adv.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "MAS 技能大厅列表响应 VO")
@Data
public class MasSkillListVO {

    @Schema(description = "主键ID", example = "100")
    private Long id;

    @Schema(description = "技能唯一编码", example = "NEW_PRODUCT_LAUNCH_V1")
    private String skillCode;

    @Schema(description = "技能名称", example = "「火箭飙升」冷启动")
    private String name;

    @Schema(description = "技能描述", example = "专为新品打造...")
    private String description;

    @Schema(description = "技能图标 url", example = "Lightning")
    private String icon;

    @Schema(description = "适用场景标签，多个用逗号分隔", example = "新品期, 拓流, 预算控制")
    private String useCaseTags;

    @Schema(description = "策略指导书结构化数据")
    private StrategyInstructionVO strategyInstruction;

    @Schema(description = "该技能允许使用的工具集集合", example = "[\"AD_SP_API\"]")
    private String requiredTools;

    @Schema(description = "技能参数定义 schema", example = "{}")
    private String paramSchema;
}
