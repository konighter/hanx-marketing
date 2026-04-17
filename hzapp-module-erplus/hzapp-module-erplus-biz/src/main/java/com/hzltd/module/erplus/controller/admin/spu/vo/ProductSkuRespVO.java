package com.hzltd.module.erplus.controller.admin.spu.vo;

import com.hzltd.module.erplus.system.dto.ProductLogisticsDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 商品 SKU Response VO")
@Data
public class ProductSkuRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "商品 SKU 名字", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉小短袖")
    private String name;

    @Schema(description = "销售价格，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "1999")
    private Integer price;

    @Schema(description = "市场价", example = "2999")
    private Integer marketPrice;

    @Schema(description = "成本价", example = "19")
    private Integer costPrice;

    @Schema(description = "SKU 编码", example = "SKU001")
    private String code;

    @Schema(description = "条形码", example = "15156165456")
    private String barCode;

    @Schema(description = "图片地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.hanx.ltd/xx.png")
    private String picUrl;

    @Schema(description = "库存", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private Integer stock;

    @Schema(description = "属性数组")
    private List<ProductSkuSaveReqVO.Property> properties;

    @Schema(description = "产品类型", example = "1")
    private Integer type;

    @Schema(description = "产品属性")
    private java.util.Map<String, Object> attributes;

    @Schema(description = "商品耗材/配件项")
    private List<MaterialItem> materialItems;

    @Schema(description = "组合成分列表")
    private List<ComboItem> comboItems;

    @Schema(description = "商品耗材/配件项")
    @Data
    public static class MaterialItem {

        @Schema(description = "耗材编号", example = "10")
        private Long materialId;

        @Schema(description = "耗材名称", example = "包装盒")
        private String materialName;

        @Schema(description = "耗材编码", example = "MAT001")
        private String materialCode;

        @Schema(description = "耗材单位", example = "个")
        private String materialUnit;

        @Schema(description = "数量", example = "1.5")
        private java.math.BigDecimal usageQuantity;

    }

    @Schema(description = "组合成分项目")
    @Data
    public static class ComboItem {

        @Schema(description = "子 SKU 编号", example = "10")
        private Long id;

        @Schema(description = "数量", example = "1")
        private Integer quantity;

        @Schema(description = "SKU 名称", example = "配件 A")
        private String name;

        @Schema(description = "SKU 编码", example = "SKU001")
        private String code;

        @Schema(description = "图片地址", example = "https://...")
        private String picUrl;
    }

    // --- SPU 相关冗余字段, 方便 SKU 视图展示 ---

    @Schema(description = "SPU 编号", example = "1")
    private Long spuId;

    @Schema(description = "SPU 名称", example = "通用商品")
    private String spuName;

    @Schema(description = "分类名称", example = "服装")
    private String categoryName;

    @Schema(description = "SPU 编码", example = "SPU001")
    private String spuCode;

    @Schema(description = "单位名称", example = "件")
    private String unitName;

    @Schema(description = "品牌名称", example = "翰展")
    private String brandName;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private java.time.LocalDateTime createTime;

}
