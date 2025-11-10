package com.hzltd.module.erplus.controller.admin.productassets.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hzltd.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 商品素材分页 Request VO")
@Data
public class ProductAssetsPageReqVO extends PageParam {

    @Schema(description = "产品名称", example = "王五")
    private Integer productName;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "素材类型", example = "2")
    private Integer type;

    @Schema(description = "素材信息")
    private String assetInfo;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}