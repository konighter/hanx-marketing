package com.hzltd.module.erplus.ai.mas.adk.tools.query;

import com.hzltd.module.erplus.ai.mas.agent.MasTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 业务数据查询 Tools (Mock 实现)
 * 提供 Listing 详情、库存状态等查询能力
 */
@Slf4j
@Component("bizQueryTools")
public class BizQueryTools {

    @MasTool("获取指定 ASIN 的 Listing 详情（标题、价格、评分、BSR、Bullet Points 等）")
    public Map<String, Object> getListingInfo(String asin) {
        log.info("[Mock] getListingInfo: asin={}", asin);
        return Map.of(
                "asin", asin,
                "title", "Premium Running Shoes for Men - Lightweight Athletic Sneakers",
                "price", 34.99,
                "rating", 4.2,
                "reviewCount", 580,
                "bsr", 12500,
                "category", "Clothing, Shoes & Jewelry > Men > Shoes",
                "bulletPoints", java.util.List.of(
                        "Lightweight and breathable mesh upper",
                        "Cushioned insole for all-day comfort",
                        "Durable rubber outsole with grip pattern",
                        "Available in 8 colors"
                ),
                "mainImageUrl", "https://example.com/mock-image.jpg"
        );
    }

    @MasTool("获取指定 ASIN 的库存状态（可用库存、入库中、预计断货天数等）")
    public Map<String, Object> getInventoryStatus(String asin) {
        log.info("[Mock] getInventoryStatus: asin={}", asin);
        return Map.of(
                "asin", asin,
                "availableQuantity", 350,
                "inboundQuantity", 200,
                "reservedQuantity", 15,
                "dailySalesAvg", 12,
                "daysOfSupply", 29,
                "fulfillmentCenter", "FBA",
                "status", "IN_STOCK"
        );
    }
}
