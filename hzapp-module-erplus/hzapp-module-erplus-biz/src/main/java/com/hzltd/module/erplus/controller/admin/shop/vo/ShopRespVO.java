package com.hzltd.module.erplus.controller.admin.shop.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hzltd.framework.excel.core.annotations.DictFormat;
import com.hzltd.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 店铺信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ShopRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3188")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("平台")
    private Integer platform;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("平台")
    private String platformName;

    @Schema(description = "区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区域")
    private Integer region;

    @Schema(description = "区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区域")
    private String regionName;

    @Schema(description = "状态", example = "2")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("common_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}