package com.hzltd.module.amz.adv.api.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CampaignBudgetRuleAssociatedRequest {

    private String campaignId;

    private List<String> ruleIds;

}
