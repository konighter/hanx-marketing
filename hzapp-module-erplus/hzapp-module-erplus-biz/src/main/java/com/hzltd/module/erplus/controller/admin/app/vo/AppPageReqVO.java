package com.hzltd.module.erplus.controller.admin.app.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hzltd.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 应用注册信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}