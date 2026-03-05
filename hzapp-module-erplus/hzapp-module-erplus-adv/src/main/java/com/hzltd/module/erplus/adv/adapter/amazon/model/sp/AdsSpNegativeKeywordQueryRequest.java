package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Amazon SP 否定关键词查询请求（广告活动级 + 广告组级复用）
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpNegativeKeywordQueryRequest extends AdsSpCommonQueryRequest {

    /** 广告活动 ID 筛选 */
    private StringFilter campaignIdFilter;

    /** 广告组 ID 筛选 */
    private StringFilter adGroupIdFilter;

    public static AdsSpNegativeKeywordQueryRequest byCampaignIds(List<String> campaignIds) {
        return AdsSpNegativeKeywordQueryRequest.builder()
                .maxResults(100)
                .campaignIdFilter(StringFilter.from(campaignIds))
                .build();
    }

    public static AdsSpNegativeKeywordQueryRequest byAdGroupIds(List<String> adGroupIds) {
        return AdsSpNegativeKeywordQueryRequest.builder()
                .maxResults(100)
                .adGroupIdFilter(StringFilter.from(adGroupIds))
                .build();
    }
}
