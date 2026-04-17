package com.hzltd.module.erplus.controller.admin.material.vo.material;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 耗材分页 Request VO")
@Data
public class ErpMaterialPageReqVO extends PageParam {

    @Schema(description = "耗材名称", example = "纸箱")
    private String name;

    @Schema(description = "耗材编码", example = "M001")
    private String code;

    @Schema(description = "耗材类型", example = "packaging")
    private String category;

    @Schema(description = "单位", example = "1")
    private Long unit;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
