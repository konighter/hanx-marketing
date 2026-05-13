package com.hzltd.module.amz.adv.controller.admin.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 亚马逊广告组分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AmzAdvAdGroupPageReqVO extends PageParam {

    @Schema(description = "店铺ID", example = "123")
    private String shopId;

    @Schema(description = "广告活动ID", example = "campaign123")
    private String campaignId;

    @Schema(description = "广告组名称", example = "主推产品组")
    private String name;

    @Schema(description = "广告组状态", example = "enabled")
    private String state;

    @Schema(description = "目标设备", example = "mobile")
    private String placement;
}