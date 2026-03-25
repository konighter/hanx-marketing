package com.hzltd.module.amz.api.adv.model.sp;

import lombok.Data;

import java.util.List;

/**
 * Amazon SP 否定关键词查询响应
 */
@Data
public class AdsSpNegativeKeywordQueryResp {
    private String nextToken;
    private List<AdsSpNegativeKeyword> negativeKeywords;
}
