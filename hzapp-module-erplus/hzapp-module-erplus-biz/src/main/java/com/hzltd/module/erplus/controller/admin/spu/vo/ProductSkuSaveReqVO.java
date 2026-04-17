package com.hzltd.module.erplus.controller.admin.spu.vo;

import com.hzltd.module.erplus.system.dto.ProductLogisticsDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "管理后台 - 商品 SKU 创建/更新 Request VO")
@Data
public class ProductSkuSaveReqVO {

    @Schema(description = "商品 SKU 编号", example = "1")
    private Long id;

    @Schema(description = "商品 SKU 编码 (外部编码/seller_sku)", example = "SKU001")
    private String code;

    @Schema(description = "产品类型", example = "1")
    private Integer type;

    @Schema(description = "组合产品子项")
    private List<ComboItem> comboItems;

    @Schema(description = "商品耗材/配件项")
    private List<MaterialItem> materialItems;

    @Schema(description = "商品 SKU 名字", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉小短袖")
    @NotEmpty(message = "商品 SKU 名字不能为空")
    private String name;

    @Schema(description = "销售价格，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "1999")
    @NotNull(message = "销售价格，单位：分不能为空")
    private Integer price;

    @Schema(description = "市场价", example = "2999")
    private Integer marketPrice;

    @Schema(description = "成本价", example = "19")
    private Integer costPrice;

    @Schema(description = "条形码", example = "15156165456")
    private String barCode;

    @Schema(description = "图片地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.hanx.ltd/xx.png")
    @NotNull(message = "图片地址不能为空")
    private String picUrl;

    @Schema(description = "库存", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    @NotNull(message = "库存不能为空")
    private Integer stock;

    @Schema(description = "属性数组")
    private List<Property> properties;

    @Schema(description = "产品属性")
    private java.util.Map<String, Object> attributes;

    @Schema(description = "商品属性")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Property {

        @Schema(description = "属性编号", example = "10")
        private Long propertyId;

        @Schema(description = "属性名字", example = "颜色")
        private String propertyName;

        @Schema(description = "属性值编号", example = "10")
        private Long valueId;

        @Schema(description = "属性值名字", example = "红色")
        private String valueName;

    }

    @Schema(description = "组合产品子项")
    @Data
    public static class ComboItem {

        @Schema(description = "子 SKU 编号", example = "10")
        private Long id;

        @Schema(description = "数量", example = "2")
        private Integer quantity;


    }

    @Schema(description = "商品耗材/配件项")
    @Data
    public static class MaterialItem {

        @Schema(description = "耗材编号", example = "10")
        private Long materialId;

        @Schema(description = "数量", example = "1.5")
        private java.math.BigDecimal usageQuantity;

    }
}
