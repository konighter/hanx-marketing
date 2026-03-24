package com.hzltd.module.amz.api.adv.model.sp;

import lombok.Data;

import java.util.List;

@Data
public class AdsSpCampaignQueryResp {

    private List<AdsSpCampaign> campaigns;

    private Integer totalResults;

    private String nextToken;

}
