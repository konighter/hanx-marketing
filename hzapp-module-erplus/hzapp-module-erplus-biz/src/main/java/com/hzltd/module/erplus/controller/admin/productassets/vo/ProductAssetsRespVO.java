package com.hzltd.module.erplus.controller.admin.productassets.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.hzltd.framework.excel.core.annotations.DictFormat;
import com.hzltd.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 商品素材 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProductAssetsRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22568")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2540")
    @ExcelProperty("产品ID")
    private Integer productId;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("产品名称")
    private Integer productName;

    @Schema(description = "标签")
    @ExcelProperty("标签")
    private String tags;

    @Schema(description = "素材类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("素材类型")
    private Integer type;

    /**
     * 来源
     */
    @Schema(description = "来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("来源")
    private Integer source;

    @Schema(description = "素材链接", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("素材链接")
    private String assetLink;

    @Schema(description = "素材信息")
    @ExcelProperty("素材信息")
    private String assetInfo;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("common_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}