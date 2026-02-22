package com.hzltd.module.erplus.adv.metadata.vo.keyword;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 广告关键词响应 VO")
@Data
public class AdsKeywordRespVO {

    @Schema(description = "关键词编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "广告账户编号", example = "1")
    private Long accountId;

    @Schema(description = "广告组编号", example = "20")
    private Long adGroupId;

    @Schema(description = "平台原始 Keyword ID", example = "K123")
    private String externalId;

    @Schema(description = "关键词文本", example = "laptop charger")
    private String keywordText;

    @Schema(description = "匹配类型", example = "EXACT")
    private String matchType;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

    @Schema(description = "平台原始状态", example = "active")
    private String platformStatus;

    @Schema(description = "出价", example = "1.20")
    private BigDecimal bid;

    @Schema(description = "同步时间")
    private LocalDateTime syncedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
