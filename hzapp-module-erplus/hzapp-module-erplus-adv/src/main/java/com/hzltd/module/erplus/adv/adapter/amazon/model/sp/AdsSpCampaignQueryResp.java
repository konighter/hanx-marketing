package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import lombok.Data;

import java.util.List;

@Data
public class AdsSpCampaignQueryResp {

    private List<AdsSpCampaign> campaigns;

    private Integer totalResults;

    private String nextToken;

}
