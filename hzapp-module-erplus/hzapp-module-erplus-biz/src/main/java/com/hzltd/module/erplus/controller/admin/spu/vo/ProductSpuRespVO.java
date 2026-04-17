package com.hzltd.module.erplus.controller.admin.spu.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.hzltd.framework.excel.core.convert.DictConvert;
import com.hzltd.framework.excel.core.convert.MoneyConvert;
import com.hzltd.module.erplus.system.dto.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 商品 SPU Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProductSpuRespVO {

    @Schema(description = "商品 SPU 编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "111")
    @ExcelProperty("商品编号")
    private Long id;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉小短袖")
    @ExcelProperty("商品名称")
    private String name;



    @Schema(description = "商品详情", requiredMode = Schema.RequiredMode.REQUIRED, example = "清凉小短袖详情")
    @ExcelProperty("商品详情")
    private String description;

    @Schema(description = "商品分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("商品分类编号")
    private Long categoryId;

    @Schema(description = "商品品牌编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("商品品牌编号")
    private Long brandId;

    @Schema(description = "商品封面图", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.hanx.ltd/xx.png")
    @ExcelProperty("商品封面图")
    private String picUrl;

    @Schema(description = "商品轮播图", requiredMode = Schema.RequiredMode.REQUIRED, example = "[https://www.hanx.ltd/xx.png, https://www.hanx.ltd/xxx.png]")
    private List<String> sliderPicUrls;

    @Schema(description = "排序字段", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("排序字段")
    private Integer sort;

    @Schema(description = "商品状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "商品状态", converter = DictConvert.class)
    private Integer status;

    @Schema(description = "商品创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-05-24 00:00:00")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    // ========== SKU 相关字段 =========

    @Schema(description = "规格类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("规格类型")
    private Integer specType;

    @Schema(description = "商品价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "1999")
    @ExcelProperty(value = "商品价格", converter = MoneyConvert.class)
    private Integer price;

    @Schema(description = "市场价，单位使用：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "199")
    @ExcelProperty(value = "市场价", converter = MoneyConvert.class)
    private Integer marketPrice;

    @Schema(description = "成本价，单位使用：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "19")
    @ExcelProperty(value = "成本价", converter = MoneyConvert.class)
    private Integer costPrice;

    @Schema(description = "商品库存", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000")
    @ExcelProperty("库存")
    private Integer stock;

    @Schema(description = "SKU 数组")
    private List<ProductSkuRespVO> skus;


    private String barCode;

    @Schema(description = "单位名称", example = "件")
    private String unitName;

    @Schema(description = "分类名称", example = "服装")
    private String categoryName;

    @Schema(description = "品牌名称", example = "翰展")
    private String brandName;

    @Schema(description = "商品 SPU 编码", example = "SPU001")
    private String code;

    @Schema(description = "单位编号", example = "1")
    private Long unitId;



    @Schema(description = "产品属性")
    private Map<String, Object> attributes;

}
