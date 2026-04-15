package com.hzltd.module.erplus.controller.admin.productpub.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 商品发布 V2 请求 (平铺/SKU 维度)")
@Data
public class ProductPublishV2Request {

    @Schema(description = "电商平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMAZON")
    @NotNull(message = "电商平台不能为空")
    private String platform;

    @Schema(description = "店铺 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "店铺 ID 不能为空")
    private Integer shopId;

    @Schema(description = "目标市场 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "ATVPDKIKX0DER")
    @NotNull(message = "目标市场不能为空")
    private String marketId;

    @Schema(description = "商品类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ASHTRAY")
    @NotNull(message = "商品类型不能为空")
    private String productType;

    @Schema(description = "卖家 SKU", requiredMode = Schema.RequiredMode.REQUIRED, example = "SKU-001")
    @NotNull(message = "卖家 SKU 不能为空")
    private String sellerSku;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "主图 URL")
    private String mainImage;

    @Schema(description = "附图 URL 列表")
    private java.util.List<String> additionalImages;

    @Schema(description = "商品属性 (平铺或嵌套 Map)")
    private Map<String, Object> attributes;

}
