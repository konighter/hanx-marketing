package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import lombok.Data;

import java.util.List;

/**
 * Amazon SP ProductAd 查询响应模型
 */
@Data
public class AdsSpProductAdQueryResp {

    /** 下一页令牌 */
    private String nextToken;

    /** 商品广告列表 */
    private List<AdsSpProductAd> productAds;

    /** 总条数 */
    private Integer totalResults;
}
