package com.hzltd.module.erplus.sys.controller.admin.languages.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - [Erplus] 语言定义 Response VO")
@Data
@ExcelIgnoreUnannotated
public class LanguagesRespVO {

    @Schema(description = "语言ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22655")
    @ExcelProperty("语言ID")
    private Integer id;

    @Schema(description = "Language code (e.g., en-US, zh-CN, fr-FR)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Language code (e.g., en-US, zh-CN, fr-FR)")
    private String code;

    @Schema(description = "语言名称 (e.g., English (US), 中文 (简体))", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("语言名称 (e.g., English (US), 中文 (简体))")
    private String name;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否启用")
    private Boolean isActive;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}