package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Amazon SP Target 查询请求（商品/品类定向）
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpTargetQueryRequest extends AdsSpCommonQueryRequest {

    /** 广告活动 ID 筛选 */
    private StringFilter campaignIdFilter;

    /** 广告组 ID 筛选 */
    private StringFilter adGroupIdFilter;

    public static AdsSpTargetQueryRequest byAdGroupIds(List<String> adGroupIds) {
        return AdsSpTargetQueryRequest.builder()
                .maxResults(100)
                .adGroupIdFilter(StringFilter.from(adGroupIds))
                .build();
    }

    public static AdsSpTargetQueryRequest from(com.hzltd.module.erplus.adv.adapter.model.AdsQueryRequest query) {
        return AdsSpTargetQueryRequest.builder()
                .nextToken(query.getNextToken())
                .maxResults(query.getLimit())
                .adGroupIdFilter(StringFilter.from(query.getAdGroupIds()))
                .campaignIdFilter(StringFilter.from(query.getCampaignIds()))
                .build();
    }
}
