package com.hzltd.module.erplus.sys.controller.admin.languages.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - [Erplus] 语言定义分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LanguagesPageReqVO extends PageParam {

    @Schema(description = "Language code (e.g., en-US, zh-CN, fr-FR)")
    private String code;

    @Schema(description = "语言名称 (e.g., English (US), 中文 (简体))", example = "芋艿")
    private String name;

    @Schema(description = "是否启用")
    private Boolean isActive;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}