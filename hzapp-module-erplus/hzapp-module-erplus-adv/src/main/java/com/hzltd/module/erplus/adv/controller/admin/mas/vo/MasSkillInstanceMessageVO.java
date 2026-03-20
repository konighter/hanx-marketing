package com.hzltd.module.erplus.adv.controller.admin.mas.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MAS 技能实例消息 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasSkillInstanceMessageVO {

    @Schema(description = "角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "user")
    private String role; // user, assistant, thought

    @Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
