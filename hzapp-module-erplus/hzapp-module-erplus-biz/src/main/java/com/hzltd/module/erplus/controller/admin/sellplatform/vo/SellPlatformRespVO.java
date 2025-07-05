package com.hzltd.module.erplus.controller.admin.sellplatform.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.ServiceMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 销售平台 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SellPlatformRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16072")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "平台名称", example = "李四")
    @ExcelProperty("平台名称")
    private String name;

    @Schema(description = "编码")
    @ExcelProperty("编码")
    private String code;

    @Schema(description = "头像")
    @ExcelProperty("头像")
    private String avatar;

    @Schema(description = "配送模式")
    @ExcelProperty("配送模式")
    private List<ServiceMode> serviceModes;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}