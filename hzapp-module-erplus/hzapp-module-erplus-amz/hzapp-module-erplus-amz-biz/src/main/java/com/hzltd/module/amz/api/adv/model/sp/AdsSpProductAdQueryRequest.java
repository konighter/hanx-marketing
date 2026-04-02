package com.hzltd.module.amz.api.adv.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hzltd.module.erplus.adv.model.AdsQueryRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Amazon SP ProductAd 查询请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpProductAdQueryRequest extends AdsSpCommonQueryRequest {

    /** 广告 ID 筛选 */
    private StringFilter adIdFilter;

    /** 广告组 ID 筛选 */
    private StringFilter adGroupIdFilter;

    /** 广告活动 ID 筛选 */
    private StringFilter campaignIdFilter;

    /** 状态筛选 */
    private StringFilter stateFilter;

    public static AdsSpProductAdQueryRequest from(AdsQueryRequest query) {
        return AdsSpProductAdQueryRequest.builder()
                .nextToken(query.getNextToken())
                .maxResults(query.getLimit())
                .adIdFilter(StringFilter.from(query.getAdIds()))
                .adGroupIdFilter(StringFilter.from(query.getAdGroupIds()))
                .campaignIdFilter(StringFilter.from(query.getCampaignIds()))
                .build();
    }
}
