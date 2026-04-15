package com.hzltd.module.amz.adv.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 亚马逊广告关键词查询结果带有动态出价与位置响应模型 VO")
@Data
public class AmzAdvKeywordRespVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "店铺ID", example = "shop123")
    private String shopId;

    @Schema(description = "亚马逊侧关键词ID", example = "amzkw123")
    private String keywordId;

    @Schema(description = "所属广告活动ID", example = "camp123")
    private String campaignId;

    @Schema(description = "所属广告组ID", example = "group123")
    private String adGroupId;

    @Schema(description = "关键词字面量", example = "summer shoes")
    private String keywordText;

    @Schema(description = "匹配类型", example = "broad")
    private String matchType;

    @Schema(description = "状态", example = "enabled")
    private String state;

    @Schema(description = "基础出价 (Base Bid)", example = "1.00")
    private Double bid;

    @Schema(description = "基于Campaign配置：展示在搜索结果顶部的动态实际出价", example = "1.50")
    private Double calculatedTopBid;

    @Schema(description = "基于Campaign配置：展示在商品详情页顶部的动态实际出价", example = "1.20")
    private Double calculatedProductPageBid;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;

}
