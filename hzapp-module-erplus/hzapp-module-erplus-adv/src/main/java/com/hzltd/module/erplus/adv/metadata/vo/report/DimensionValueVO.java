package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Schema(description = "管理后台 - 广告报表维度值 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionValueVO {

    @Schema(description = "维度标识", example = "campaign_id")
    private String key;

    @Schema(description = "原始维度的值", example = "12345")
    private String value;

    @Schema(description = "显示名称(通过缓存等后置填充)", example = "Summer_SP_Auto")
    private String label;
}
