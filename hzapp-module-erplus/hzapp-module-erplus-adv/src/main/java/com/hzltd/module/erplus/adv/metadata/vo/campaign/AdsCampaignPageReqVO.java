package com.hzltd.module.erplus.adv.metadata.vo.campaign;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 广告计划分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdsCampaignPageReqVO extends PageParam {

    @Schema(description = "店铺编号", example = "1")
    private Long shopId;

    @Schema(description = "广告账户编号(已弃用，请使用 shopId)", example = "1")
    @Deprecated
    private Long accountId;

    @Schema(description = "平台原始 Campaign ID", example = "C123")
    private String externalId;

    @Schema(description = "计划名称", example = "Autumn Sale")
    private String name;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
