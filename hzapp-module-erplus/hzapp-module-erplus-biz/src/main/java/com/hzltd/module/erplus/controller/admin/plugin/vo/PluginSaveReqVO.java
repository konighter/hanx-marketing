package com.hzltd.module.erplus.controller.admin.plugin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 插件新增/修改 Request VO")
@Data
public class PluginSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23616")
    private Integer id;

    @Schema(description = "名称", example = "芋艿")
    private String name;

    @Schema(description = "插件标识")
    private String pluginKey;

    @Schema(description = "状态", example = "1")
    private Integer status;

}