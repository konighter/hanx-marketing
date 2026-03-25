package com.hzltd.module.erplus.adv.metadata.vo.adgroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 广告组响应 VO")
@Data
public class AdsAdGroupRespVO {

    @Schema(description = "广告组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "广告账户编号", example = "1")
    private Long accountId;

    @Schema(description = "广告计划编号", example = "10")
    private Long campaignId;

    @Schema(description = "平台原始 AdGroup ID", example = "G123")
    private String externalId;

    @Schema(description = "广告组名称", example = "Electronics Group")
    private String name;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

    @Schema(description = "原始平台状态", example = "active")
    private String platformStatus;

    @Schema(description = "默认出价", example = "1.50")
    private BigDecimal defaultBid;

    @Schema(description = "出价策略", example = "AUTO_BID")
    private String bidStrategy;

    @Schema(description = "投放定向方式", example = "KEYWORD")
    private String targetingType;

    @Schema(description = "平台扩展字段 (JSON)")
    private Object extData;

    @Schema(description = "同步时间")
    private LocalDateTime syncedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "账号平台", example = "AMAZON")
    private String platform;

    @Schema(description = "扩展属性")
    private Map<String, Object> attributes;

}
