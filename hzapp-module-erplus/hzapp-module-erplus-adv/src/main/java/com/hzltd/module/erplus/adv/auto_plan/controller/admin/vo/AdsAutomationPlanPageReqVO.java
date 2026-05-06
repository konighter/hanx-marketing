package com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 广告自动化计划分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdsAutomationPlanPageReqVO extends PageParam {

    @Schema(description = "计划名称", example = "我的")
    private String name;

    @Schema(description = "店铺编号", example = "1")
    private Long shopId;

    @Schema(description = "状态", example = "RUNNING")
    private String status;

    @Schema(description = "商品 SKU/ASIN")
    private String sku;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
