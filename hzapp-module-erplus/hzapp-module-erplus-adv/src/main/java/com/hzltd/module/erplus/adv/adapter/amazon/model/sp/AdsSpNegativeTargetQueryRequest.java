package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Amazon SP 否定定向查询请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpNegativeTargetQueryRequest extends AdsSpCommonQueryRequest {

    /** 广告活动 ID 筛选 */
    private StringFilter campaignIdFilter;

    /** 广告组 ID 筛选 */
    private StringFilter adGroupIdFilter;

    public static AdsSpNegativeTargetQueryRequest byAdGroupIds(List<String> adGroupIds) {
        return AdsSpNegativeTargetQueryRequest.builder()
                .maxResults(100)
                .adGroupIdFilter(StringFilter.from(adGroupIds))
                .build();
    }
}
