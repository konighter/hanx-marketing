package com.hzltd.module.amz.api.adv.model.sp;

import lombok.Data;

import java.util.List;

@Data
public class AdsSpAdGroupQueryResp {

    private List<AdsSpAdGroup> adGroups;

    private Integer totalResults;

    private String nextToken;

}
