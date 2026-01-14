package com.hzltd.module.erplus.controller.admin.warehouse.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - ERP 仓库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WarehouseRespVO {

    @Schema(description = "仓库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21598")
    @ExcelProperty("仓库编号")
    private Long id;

    @Schema(description = "仓库名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("仓库名称")
    private String name;

    @Schema(description = "类型: 平台仓/海外仓/家庭仓/活动仓", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("类型: 平台仓/海外仓/家庭仓/活动仓")
    private Integer type;

    @Schema(description = "店铺", example = "15932")
    @ExcelProperty("店铺")
    private Integer shopId;

    @Schema(description = "平台ID", example = "16669")
    @ExcelProperty("平台ID")
    private Integer platformId;

    @Schema(description = "市场", example = "9111")
    @ExcelProperty("市场")
    private String marketId;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "负责人")
    @ExcelProperty("负责人")
    private String principal;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("开启状态")
    private Integer status;

    @Schema(description = "是否默认", example = "1")
    @ExcelProperty("是否默认")
    private Boolean defaultStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}