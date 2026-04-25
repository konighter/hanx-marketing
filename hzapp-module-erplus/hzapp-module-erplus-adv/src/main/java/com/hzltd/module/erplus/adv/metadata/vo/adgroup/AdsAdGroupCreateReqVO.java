package com.hzltd.module.erplus.adv.metadata.vo.adgroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 广告组创建 Request VO")
@Data
public class AdsAdGroupCreateReqVO {

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long campaignId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "默认竞价", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double defaultBid;

    @Schema(description = "属性数据")
    private Map<String, Object> attributes;

}
