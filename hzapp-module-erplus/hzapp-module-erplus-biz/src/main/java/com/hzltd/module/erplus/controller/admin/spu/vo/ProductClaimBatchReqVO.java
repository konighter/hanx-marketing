package com.hzltd.module.erplus.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 商品认领新增/修改 Request VO")
@Data
public class ProductClaimBatchReqVO {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "23910")
    private Long spuId;

    @Schema(description = "sku信息")
    private List<ProductSkuVO> skus;

    @Schema(description = "变种类型", example = "1")
    private Boolean specType;

    @NotNull(message = "平台不能为空")
    @Schema(description = "平台")
    private Integer platform;

    @NotEmpty(message = "语言不能为空")
    @Schema(description = "语言")
    private String language;

    @NotNull(message = "站点不能为空")
    @Schema(description = "站点", example = "25354")
    private Integer sellZone;


    @NotEmpty(message = "店铺不能为空")
    @Schema(description = "店铺", example = "25354")
    private List<Integer> shopId;

    @NotEmpty(message = "品类不能为空")
    @Schema(description = "品类")
    private String category;

    @NotNull(message = "品牌不能为空")
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