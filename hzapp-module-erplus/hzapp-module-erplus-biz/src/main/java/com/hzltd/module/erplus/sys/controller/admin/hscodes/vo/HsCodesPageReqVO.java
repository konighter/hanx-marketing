package com.hzltd.module.erplus.sys.controller.admin.hscodes.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - [Erplus] 海关编码(HS Code)分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HsCodesPageReqVO extends PageParam {

    @Schema(description = "HS Code编码 (可能包含点)")
    private String code;

    @Schema(description = "编码描述", example = "你猜")
    private String description;

    @Schema(description = "关联的内部产品分类ID (方便查找)", example = "23692")
    private Long categoryId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}