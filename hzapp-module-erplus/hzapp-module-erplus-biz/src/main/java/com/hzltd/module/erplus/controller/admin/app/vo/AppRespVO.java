package com.hzltd.module.erplus.controller.admin.app.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应用注册信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7249")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "名称", example = "王五")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "平台ID", example = "11420")
    @ExcelProperty("平台ID")
    private Integer platformId;

    @Schema(description = "平台名称", example = "11420")
    @ExcelProperty("平台名称")
    private String platformName;

    @Schema(description = "AppId", example = "7669")
    @ExcelProperty("AppId")
    private String appId;

    @Schema(description = "应用Key")
    @ExcelProperty("应用Key")
    private String appKey;

    @Schema(description = "应用密钥")
    @ExcelProperty("应用密钥")
    private String appSecret;

    @Schema(description = "状态")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}