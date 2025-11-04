package com.hzltd.module.erplus.sys.controller.admin.hscodes.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - [Erplus] 海关编码(HS Code)新增/修改 Request VO")
@Data
public class HsCodesSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21432")
    private Long id;

    @Schema(description = "HS Code编码 (可能包含点)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "HS Code编码 (可能包含点)不能为空")
    private String code;

    @Schema(description = "编码描述", example = "你猜")
    private String description;

    @Schema(description = "关联的内部产品分类ID (方便查找)", example = "23692")
    private Long categoryId;

}