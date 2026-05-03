package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 亚马逊广告 帮助 - 产品元数据 Request VO")
@Data
public class AmzAdvHelpProductMetadataReqVO {

    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "广告类型 (SP, SB, SD)", example = "SP")
    private String adType;

    @Schema(description = "ASIN 列表")
    private List<String> asins;

    @Schema(description = "SKU 列表")
    private List<String> skus;

    @Schema(description = "搜索字符串", example = "jean")
    private String searchStr;

    @Schema(description = "是否检查资格", example = "false")
    private Boolean checkEligibility = false;

    @Schema(description = "是否检查产品详情", example = "false")
    private Boolean checkItemDetails = false;

    @Schema(description = "分页索引", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "分页索引不能为空")
    private Integer pageIndex;

    @Schema(description = "分页大小", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "分页大小不能为空")
    private Integer pageSize;

    @Schema(description = "排序字段", example = "SUGGESTED")
    private String sortBy;

    @Schema(description = "排序顺序", example = "DESC")
    private String sortOrder;

    @Schema(description = "语言区域", example = "zh_CN")
    private String locale;

}
