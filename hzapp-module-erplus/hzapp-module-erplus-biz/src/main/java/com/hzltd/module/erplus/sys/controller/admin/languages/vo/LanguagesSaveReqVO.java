package com.hzltd.module.erplus.sys.controller.admin.languages.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - [Erplus] 语言定义新增/修改 Request VO")
@Data
public class LanguagesSaveReqVO {

    @Schema(description = "语言ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22655")
    private Integer id;

    @Schema(description = "Language code (e.g., en-US, zh-CN, fr-FR)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Language code (e.g., en-US, zh-CN, fr-FR)不能为空")
    private String code;

    @Schema(description = "语言名称 (e.g., English (US), 中文 (简体))", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "语言名称 (e.g., English (US), 中文 (简体))不能为空")
    private String name;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否启用不能为空")
    private Boolean isActive;

}