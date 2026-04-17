package com.hzltd.module.erplus.controller.admin.spu.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 商品 SPU 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductSpuPageReqVO extends PageParam {

    /**
     * 草稿
     */
    public static final Integer DRAFT = 0;
    /**
     * 出售中商品
     */
    public static final Integer FOR_SALE = 1;

    /**
     * 下架
     */
    public static final Integer OFF_SALE = 2;

    /**
     * 已售空商品
     */
    public static final Integer SOLD_OUT = 3;

    /**
     * 警戒库存
     */
    public static final Integer ALERT_STOCK = 4;

    /**
     * 商品回收站
     */
    public static final Integer ARCHIVED = 5;

    /**
     * 单规格 SPU
     */
    public static final Integer SINGLE_SPEC = 6;
    /**
     * 多规格 SPU
     */
    public static final Integer MULTI_SPEC = 7;

    /**
     * 普通 SKU
     */
    public static final Integer ORDINARY_SKU = 8;
    /**
     * 组合 SKU
     */
    public static final Integer COMBO_SKU = 9;

    @Schema(description = "商品名称", example = "清凉小短袖")
    private String name;

    @Schema(description = "前端请求的tab类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer tabType;

    @Schema(description = "分类编号", example = "1")
    private Long categoryId;

    @Schema(description = "品牌编号", example = "1")
    private Long brandId;

    @Schema(description = "商品状态", example = "1")
    private Integer status;

    @Schema(description = "搜索类型", example = "SKU")
    private String searchType;

    @Schema(description = "搜索值", example = "6901234567890")
    private String searchValue;

    @Schema(description = "展示模式", example = "SKU")
    private String viewMode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
