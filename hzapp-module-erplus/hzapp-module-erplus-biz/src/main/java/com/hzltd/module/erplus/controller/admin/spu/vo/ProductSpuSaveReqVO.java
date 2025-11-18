package com.hzltd.module.erplus.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 商品 SPU 新增/更新 Request VO")
@Data
public class ProductSpuSaveReqVO {

    @Schema(description = "商品编号", example = "1")
    private Long id;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉小短袖")
    @NotEmpty(message = "商品名称不能为空")
    private String name;

    @Schema(description = "商品分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    @Schema(description = "商品品牌编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品品牌不能为空")
    private Long brandId;

    @Schema(description = "关键字", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉丝滑不出汗")
    @NotEmpty(message = "商品关键字不能为空")
    private List<String> keyword;

    @Schema(description = "商品简介", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉小短袖简介")
    @NotEmpty(message = "商品简介不能为空")
    private List<String> introduction;

    @Schema(description = "商品详情", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉小短袖详情")
    @NotEmpty(message = "商品详情不能为空")
    private String description;

    @Schema(description = "商品封面图", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xx.png")
    @NotEmpty(message = "商品封面图不能为空")
    private String picUrl;

    @Schema(description = "商品轮播图", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "[https://www.iocoder.cn/xx.png, https://www.iocoder.cn/xxx.png]")
    private List<String> sliderPicUrls;

    @Schema(description = "排序字段", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品排序字段不能为空")
    private Integer sort;

    // ========== SKU 相关字段 =========

    @Schema(description = "规格类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "商品规格类型不能为空")
    private Boolean specType;

    @Schema(description = "SKU 数组")
    @Valid
    private List<ProductSkuSaveReqVO> skus;

    // ========== 物流 相关字段 =========

    @Schema(description = "净品规格")
    private Dimension itemDim;

    @Schema(description = "包裹规格")
    private Dimension pkgDim;

    @Schema(description = "单箱规格")
    private Dimension boxDim;

    @Schema(description = "单箱数量", example = "10")
    private Integer inboxnum;


    // ========== 合规和资质 相关字段 ==========
    @Schema(description = "认证信息")
    private List<Certification> certifications;

    @Schema(description = "安全标准", example = "[\"ISO9001\",\"CE\"]")
    private List<String> safetyStandards;

    @Schema(description = "安全警告", example = "[\"远离火源\",\"儿童不宜\"]")
    private List<String> safetyWarnings;

    @Schema(description = "材料信息")
    private List<Material> materials;

    @Schema(description = "有害物质", example = "[\"铅\",\"汞\"]")
    private List<String> hazardousSubstances;

    @Schema(description = "环保认证", example = "[\"RoHS\",\"REACH\"]")
    private List<String> environmentalCertifications;

    @Schema(description = "包装材料", example = "[\"纸箱\",\"泡沫\"]")
    private List<String> packagingMaterials;

    @Schema(description = "适用法规", example = "[\"FDA\",\"EPA\"]")
    private List<String> applicableRegulations;

    @Schema(description = "限制地区", example = "[\"美国\",\"欧盟\"]")
    private List<String> restrictedRegions;

    @Schema(description = "特殊许可证", example = "[\"医疗器械许可证\",\"食品经营许可证\"]")
    private List<String> specialLicenses;

    @Schema(description = "质量报告")
    private List<QualityReport> qualityReports;

    @Schema(description = "产品属性")
    private Map<String, Object> attributes;





    // ========== 内部类定义 ==========
    @Schema(description = "尺寸信息")
    @Data
    public static class Dimension {
        @Schema(description = "长度", example = "10.0")
        private Double length;

        @Schema(description = "宽度", example = "5.0")
        private Double width;

        @Schema(description = "高度", example = "2.0")
        private Double height;

        @Schema(description = "重量", example = "1.5")
        private Double weight;

        @Schema(description = "商品体积", example = "100.0")
        private Double volume;

        @Schema(description = "长度单位", example = "cm")
        private String unit;

        @Schema(description = "重量单位", example = "kg")
        private String weightUnit;
    }

    @Schema(description = "商品属性")
    @Data
    public static class ProductAttribute {
        @Schema(description = "属性名称", example = "颜色")
        private String name;

        @Schema(description = "属性值", example = "红色")
        private String value;

        @Schema(description = "属性类型", example = "text")
        private String type;
    }

    @Schema(description = "包装尺寸")
    @Data
    public static class PackageDimensions {
        @Schema(description = "长度", example = "10.0")
        private Double length;

        @Schema(description = "宽度", example = "5.0")
        private Double width;

        @Schema(description = "高度", example = "2.0")
        private Double height;

        @Schema(description = "尺寸单位", example = "cm")
        private String unit;

        @Schema(description = "重量", example = "1.5")
        private Double weight;

        @Schema(description = "重量单位", example = "kg")
        private String weightUnit;
    }

    @Schema(description = "认证信息")
    @Data
    public static class Certification {
        @Schema(description = "认证名称", example = "CE认证")
        private String name;

        @Schema(description = "认证值", example = "CE123456")
        private String value;
    }

    @Schema(description = "材料信息")
    @Data
    public static class Material {
        @Schema(description = "材料名称", example = "棉")
        private String name;

        @Schema(description = "含量百分比", example = "80.0")
        private Double percentage;

        @Schema(description = "材料类型", example = "天然纤维")
        private String type;
    }

    @Schema(description = "质量报告")
    @Data
    public static class QualityReport {
        @Schema(description = "报告类型", example = "质量检测报告")
        private String reportType;

        @Schema(description = "报告编号", example = "QR202312001")
        private String reportNumber;

        @Schema(description = "签发日期", example = "2023-12-01")
        private String issueDate;

        @Schema(description = "有效期至", example = "2024-12-01")
        private String validUntil;

        @Schema(description = "签发机构", example = "SGS")
        private String issuingAuthority;
    }

}
