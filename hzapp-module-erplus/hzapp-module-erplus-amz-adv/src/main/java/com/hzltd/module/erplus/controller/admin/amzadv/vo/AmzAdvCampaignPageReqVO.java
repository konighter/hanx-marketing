package com.hzltd.module.erplus.controller.admin.amzadv.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 亚马逊广告活动分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AmzAdvCampaignPageReqVO extends PageParam {

    @Schema(description = "店铺ID", example = "123")
    private String shopId;

    @Schema(description = "广告活动名称", example = "夏季促销活动")
    private String name;

    @Schema(description = "广告活动状态", example = "enabled")
    private String state;

    @Schema(description = "广告类型", example = "sponsoredProducts")
    private String campaignType;

    @Schema(description = "目标市场", example = "auto")
    private String targetingType;
}