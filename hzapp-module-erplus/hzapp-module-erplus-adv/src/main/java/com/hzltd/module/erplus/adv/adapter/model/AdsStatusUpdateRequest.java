package com.hzltd.module.erplus.adv.adapter.model;

import lombok.Data;

import java.util.List;

@Data
public class AdsStatusUpdateRequest {

    private String campaignId;

    private String adGroupId;

    private String adId;

    private String status;

}
