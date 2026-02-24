package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import lombok.Data;

import java.util.List;

/**
 * Amazon SP Keyword 查询响应模型
 */
@Data
public class AdsSpKeywordQueryResp {

    /** 关键词列表 */
    private List<AdsSpKeyword> keywords;

    /** 下一页令牌 */
    private String nextToken;

    /** 总条数 */
    private Integer totalResults;
}
