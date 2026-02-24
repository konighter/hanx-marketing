package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hzltd.module.erplus.adv.adapter.model.AdsQueryRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Amazon SP AdGroup 查询请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpAdGroupQueryRequest extends AdsSpCommonQueryRequest {

    /** 广告组 ID 筛选 */
    
    private StringFilter adGroupIdFilter;

    /** 广告活动 ID 筛选 */
    private StringFilter campaignIdFilter;

    /** 状态筛选 */
    private StringFilter stateFilter;

    /** 名称筛选 */
    private StringFilter nameFilter;

    public static AdsSpAdGroupQueryRequest from(AdsQueryRequest query) {
        return AdsSpAdGroupQueryRequest.builder()
                .nextToken(query.getNextToken())
                .maxResults(query.getLimit())
                .adGroupIdFilter(StringFilter.from(query.getAdGroupIds()))
                .campaignIdFilter(StringFilter.from(query.getCampaignIds()))
                .nameFilter(StringFilter.from(query.getAdGroupNames()))
                .build();
    }
}
