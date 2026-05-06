package com.hzltd.module.erplus.adv.automation.service;

import com.hzltd.module.erplus.adv.automation.dto.BidRangeDTO;

/**
 * 广告利润模型服务
 * 根据商品成本、售价、ACOS 目标计算出价限制
 *
 * @author antigravity
 */
public interface AdsProfitModelService {

    /**
     * 计算特定 SKU 在指定平台和目标 ACOS 下的出价范围
     * 
     * @param sku 卖家 SKU
     * @param platform 平台 (AMAZON/META)
     * @param targetAcos 目标 ACOS (百分比，如 0.25 表示 25%)
     * @return 出价范围约束
     */
    BidRangeDTO calculateBidRange(String sku, String platform, Double targetAcos);

}
