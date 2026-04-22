package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 广告多维报表查询请求 VO")
@Data
public class AdsReportQueryReqVO {

    // ===== 必填：时间与隔离边界 =====
    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    // ===== 核心：分组与指标 =====
    @Schema(description = "聚合维度（分组依据），例如: ['platform', 'campaign_id', 'date']")
    private List<String> dimensions;

    @Schema(description = "请求的基础/平台指标，例如: ['impressions', 'spend', 'sales']")
    private List<String> metrics;

    // ===== 明确枚举的过滤条件 (支持下钻与切片) =====
    @Schema(description = "广告平台列表", example = "['AMAZON', 'META']")
    private List<String> platforms;

    @Schema(description = "广告活动ID列表")
    private List<String> campaignIds;

    @Schema(description = "广告组ID列表")
    private List<String> adGroupIds;

    @Schema(description = "广告ID列表")
    private List<String> adIds;

    @Schema(description = "关键词/匹配对象ID列表")
    private List<String> keywordIds;

    @Schema(description = "产品/ASIN列表")
    private List<String> productIds;

    @Schema(description = "广告位列表", example = "['Top of Search', 'Rest of Search']")
    private List<String> placements;

    // ===== 展现控制 =====
    @Schema(description = "时间聚合粒度: DAY, WEEK, MONTH, TOTAL", example = "DAY")
    private String interval;

    @Schema(description = "用于图表等非分页防超载限制 (例如: 1000)")
    private Integer limit;

    @Schema(description = "分页 - 页码", example = "1")
    private Integer pageNo;

    @Schema(description = "分页 - 每页条数", example = "20")
    private Integer pageSize;

    @Schema(description = "排序字段 (必须是基础维度或基础指标)", example = "spend")
    private String orderBy;

    @Schema(description = "是否升序", example = "false")
    private Boolean isAsc;
}
