package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 广告维度 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsDimensionVO {

    @Schema(description = "维度标识", example = "campaignName")
    private String key;

    @Schema(description = "维度名称", example = "广告系列")
    private String name;

    @Schema(description = "维度数值", example = "搜索广告系列-基础款")
    private Object value;

}
