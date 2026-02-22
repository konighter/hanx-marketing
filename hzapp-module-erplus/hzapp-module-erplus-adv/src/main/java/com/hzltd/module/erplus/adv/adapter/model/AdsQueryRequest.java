package com.hzltd.module.erplus.adv.adapter.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AdsQueryRequest extends BaseQueryRequest {

    private List<String> campaignIds;

    private List<String> campaignNames;

    private List<String> adGroupIds;

    private List<String> adGroupNames;

    private List<String> adIds;

    private List<String> adNames;

    private List<String> targetIds;

    private List<String> targetNames;

    private Map<String, Object> extraParam;



}
