package com.hzltd.module.erplus.adv.metadata.vo.report;

import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "管理后台 - 广告绩效请求 VO")
@Data
public class AdsPerformanceReqVO {

    @Schema(description = "店铺编号", example = "1")
    private Long shopId;

    @Schema(description = "广告账户编号(已弃用，请使用 shopId)", example = "1")
    @Deprecated
    private Long accountId;

    @Schema(description = "实体类型", example = "CAMPAIGN")
    private AdsEntityTypeEnum entityType;

    @Schema(description = "实体编号", example = "1")
    private Long entityId;

    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "时间粒度: day / week / month", example = "day")
    private String timeUnit;
}
