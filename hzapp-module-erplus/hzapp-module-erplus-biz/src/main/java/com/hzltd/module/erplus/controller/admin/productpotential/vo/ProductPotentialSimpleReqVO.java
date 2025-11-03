package com.hzltd.module.erplus.controller.admin.productpotential.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 选品提案分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductPotentialSimpleReqVO extends PageParam {
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12370")
    @NotNull(message = "ID不能为空")
    private Integer id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "分析意见")
    private String analysisOpinion;

    @Schema(description = "总结意见")
    private String debriefOpinion;

    @Schema(description = "备注", example = "1")
    private String marks;

}