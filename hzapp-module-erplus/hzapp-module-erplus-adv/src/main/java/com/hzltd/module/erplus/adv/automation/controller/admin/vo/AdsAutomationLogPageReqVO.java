package com.hzltd.module.erplus.adv.automation.controller.admin.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 广告自动化日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdsAutomationLogPageReqVO extends PageParam {

    @Schema(description = "计划编号", example = "1024")
    private Long planId;

}
