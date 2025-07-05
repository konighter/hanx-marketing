package com.hzltd.module.erplus.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 商品认领新增/修改 Request VO")
@Data
public class ProductClaimSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10557")
    private Integer id;

    @Schema(description = "商品ID", example = "23910")
    private Integer spuId;

    @Schema(description = "sku信息")
    private String skuInfo;

    @Schema(description = "变种类型", example = "1")
    private Boolean specType;

    @Schema(description = "平台类型")
    private Integer platform;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "站点", example = "25354")
    private Integer sellZone;

    @Schema(description = "品类")
    private String category;

    @Schema(description = "品牌", example = "15850")
    private Integer brandId;

    @Schema(description = "售价，多变种填最大值", example = "3830")
    private Integer sellPrice;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "扩展信息")
    private String extra;

    @Schema(description = "状态， 0-认领， 1-已同步， 9-同步失败", example = "2")
    private Integer status;

}