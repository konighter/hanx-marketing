package com.hzltd.module.erplus.controller.admin.amzadv.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 亚马逊广告出价策略分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AmzAdvBidStrategyPageReqVO extends PageParam {

    @Schema(description = "店铺ID", example = "123")
    private String shopId;

    @Schema(description = "策略名称", example = "动态出价策略")
    private String name;

    @Schema(description = "策略类型", example = "dynamic")
    private String strategyType;

    @Schema(description = "广告活动类型", example = "sponsoredProducts")
    private String campaignType;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
}