package com.hzltd.module.erplus.controller.admin.spu.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 商品认领 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProductClaimRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10557")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "商品ID", example = "23910")
    @ExcelProperty("商品ID")
    private Integer spuId;

    @Schema(description = "变种类型", example = "1")
    @ExcelProperty("变种类型")
    private Boolean specType;

    private String skuInfo;

    private List<ProductSkuVO> skus;

    @Schema(description = "平台")
    @ExcelProperty("平台")
    private Integer platform;

    @Schema(description = "平台名称")
    @ExcelProperty("平台名称")
    private String platformName;

    @Schema(description = "语言")
    @ExcelProperty("语言")
    private String language;

    @Schema(description = "站点", example = "25354")
    @ExcelProperty("站点")
    private Integer sellZone;

    @Schema(description = "站点名称", example = "25354")
    @ExcelProperty("站点名称")
    private String sellZoneName;

    @Schema(description = "品类")
    @ExcelProperty("品类")
    private String category;

    @Schema(description = "品牌", example = "15850")
    @ExcelProperty("品牌")
    private Long brandId;

    @Schema(description = "品牌", example = "15850")
    @ExcelProperty("品牌名称")
    private String brandName;

    @Schema(description = "售价，多变种填最大值", example = "3830")
    @ExcelProperty("售价，多变种填最大值")
    private Integer sellPrice;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currency;

    @Schema(description = "扩展信息")
    @ExcelProperty("扩展信息")
    private String extra;

    @Schema(description = "状态， 0-认领， 1-已同步， 9-同步失败", example = "2")
    @ExcelProperty("状态， 0-认领， 1-已同步， 9-同步失败")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}