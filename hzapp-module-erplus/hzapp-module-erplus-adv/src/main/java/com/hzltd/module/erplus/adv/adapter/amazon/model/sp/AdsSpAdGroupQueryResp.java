package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import lombok.Data;

import java.util.List;

@Data
public class AdsSpAdGroupQueryResp {

    private List<AdsSpAdGroup> adGroups;

    private Integer totalResults;

    private String nextToken;

}
