package com.hzltd.module.erplus.controller.admin.productMonitor.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hzltd.framework.common.pojo.PageParam;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 产品监控指标分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductMetricsDataPageReqVO extends PageParam {

    @NotNull(message = "监控任务ID不能为空")
    @Schema(description = "监控任务ID", example = "16212")
    private Long monitorId;

    @NotNull(message = "产品ID不能为空")
    @Schema(description = "产品ID", example = "16790")
    private String productId;

    @Schema(description = "日期(yyyyMMdd)")
    private String[] datekey;

    @Schema(description = "指标")
    private List<String> metrics;



}