package com.hzltd.module.erplus.adv.adapter.amazon.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Campaign 列表查询响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmzCampaignListResponse {
    private List<AmzCampaign> campaigns;
    private String nextToken;
}
