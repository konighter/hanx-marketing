package com.hzltd.module.erplus.controller.admin.warehouse.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hzltd.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 仓库分页 Request VO")
@Data
public class WarehousePageReqVO extends PageParam {

    @Schema(description = "仓库名称", example = "张三")
    private String name;

    @Schema(description = "类型: 平台仓/海外仓/家庭仓/活动仓", example = "1")
    private Integer type;

    @Schema(description = "店铺", example = "15932")
    private Integer shopId;

    @Schema(description = "平台ID", example = "16669")
    private Integer platformId;

    @Schema(description = "市场", example = "9111")
    private String marketId;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "负责人")
    private String principal;

    @Schema(description = "开启状态", example = "2")
    private Integer status;

    @Schema(description = "是否默认", example = "1")
    private Boolean defaultStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}