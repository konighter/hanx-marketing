package com.hzltd.module.amz.api.adv.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Campaign 列表查询请求
 * 对应 Amazon V1 POST /campaigns/list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmzCampaignListRequest {

    private IncludeFilter adProductFilter;
    private IncludeFilter campaignIdFilter;
    private IncludeFilter goalFilter;
    private IncludeFilter marketplaceScopeFilter;
    private IncludeFilter portfolioIdFilter;
    private IncludeFilter stateFilter;
    private NameFilter nameFilter;

    private Integer maxResults;
    private String nextToken;

    // ==================== Filter Types ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IncludeFilter {
        private List<String> include;

        public static IncludeFilter of(String... value) {
            IncludeFilter filter = new IncludeFilter();
            filter.setInclude(List.of(value));
            return filter;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NameFilter {
        private List<String> include;
        /** 匹配类型: BROAD_MATCH / EXACT_MATCH */
        private String queryTermMatchType;
    }
}
