package com.hzltd.module.erplus.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 应用注册信息新增/修改 Request VO")
@Data
public class AppSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7249")
    private Integer id;

    @Schema(description = "名称", example = "王五")
    private String name;

    @Schema(description = "平台ID", example = "11420")
    private Long platformId;

    @Schema(description = "AppId", example = "7669")
    private String appId;

    @Schema(description = "应用Key")
    private String appKey;

    @Schema(description = "应用密钥")
    private String appSecret;

    @Schema(description = "状态")
    private Integer status;
}