package com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 广告自动化模版分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdsAutomationTemplatePageReqVO extends PageParam {

    @Schema(description = "模版名称", example = "流转")
    private String name;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
