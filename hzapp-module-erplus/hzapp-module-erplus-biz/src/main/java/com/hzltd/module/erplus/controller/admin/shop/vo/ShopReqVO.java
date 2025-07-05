package com.hzltd.module.erplus.controller.admin.shop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 店铺信息分页 Request VO")
@Data
@ToString(callSuper = true)
public class ShopReqVO{

    @Schema(description = "ID", example = "1")
    private Integer id;

    @Schema(description = "名称", example = "赵六")
    private String name;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "区域")
    private String region;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}