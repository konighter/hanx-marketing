package com.hzltd.module.amz.api.adv.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hzltd.module.adv.model.AdsQueryRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Amazon SP Keyword 查询请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpKeywordQueryRequest extends AdsSpCommonQueryRequest {

    /** 关键词 ID 筛选 */
    private StringFilter keywordIdFilter;

    /** 广告组 ID 筛选 */
    private StringFilter adGroupIdFilter;

    /** 广告活动 ID 筛选 */
    private StringFilter campaignIdFilter;

    /** 状态筛选 */
    private StringFilter stateFilter;

    public static AdsSpKeywordQueryRequest from(AdsQueryRequest query) {
        return AdsSpKeywordQueryRequest.builder()
                .nextToken(query.getNextToken())
                .maxResults(query.getLimit())
                .keywordIdFilter(StringFilter.from(query.getKeywordIds()))
                .adGroupIdFilter(StringFilter.from(query.getAdGroupIds()))
                .campaignIdFilter(StringFilter.from(query.getCampaignIds()))
                .build();
    }
}
