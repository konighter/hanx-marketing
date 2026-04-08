package com.hzltd.module.erplus.controller.admin.listing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 亚马逊刊登字段联动规则 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzListingFieldRuleVO {

    @Schema(description = "规则类型: visibility, requirement", example = "visibility")
    private String type;

    @Schema(description = "触发条件 (JS 表达式风格)", example = "binding == 'Hardcover'")
    private String condition;

    @Schema(description = "动作: show, hide, required, optional", example = "show")
    private String action;

}
