package com.hzltd.module.adv.model;

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

    private List<String> keywordIds;

    private List<String> keywordTexts;

    private List<String> targetIds;

    private Map<String, Object> extraParam;

}
