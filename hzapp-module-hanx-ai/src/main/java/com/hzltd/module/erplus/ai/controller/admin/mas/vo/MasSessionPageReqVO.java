package com.hzltd.module.erplus.ai.controller.admin.mas.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MAS 会话分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MasSessionPageReqVO extends PageParam {

    @Schema(description = "任务目标关键词", example = "广告分析")
    private String goal;

    @Schema(description = "会话状态", example = "COMPLETED")
    private String status;

}
