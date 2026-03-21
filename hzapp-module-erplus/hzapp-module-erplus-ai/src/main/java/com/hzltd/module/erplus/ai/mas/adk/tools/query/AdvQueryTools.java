package com.hzltd.module.erplus.ai.mas.adk.tools.query;

import com.hzltd.module.erplus.ai.mas.agent.MasTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 广告数据查询 Tools (Mock 实现)
 * 提供广告活动、指标、关键词、竞品等数据的查询能力
 */
@Slf4j
@Component("advQueryTools")
public class AdvQueryTools {

    @MasTool("获取指定 ASIN 的广告活动列表及核心指标（花费、点击、转化率、ACOS 等）")
    public Map<String, Object> getAdCampaigns(String asin, int days) {
        log.info("[Mock] getAdCampaigns: asin={}, days={}", asin, days);
        return Map.of(
                "asin", asin,
                "campaigns", java.util.List.of(
                        Map.of("campaignId", "C001", "name", "SP-Auto-" + asin, "status", "ENABLED",
                                "budget", 50.0, "spend", 35.2, "clicks", 120, "impressions", 8500,
                                "orders", 8, "acos", 0.22, "roas", 4.5),
                        Map.of("campaignId", "C002", "name", "SP-Manual-" + asin, "status", "ENABLED",
                                "budget", 80.0, "spend", 62.1, "clicks", 210, "impressions", 15000,
                                "orders", 15, "acos", 0.18, "roas", 5.6)
                ),
                "period", days + " days"
        );
    }

    @MasTool("获取指定 ASIN 近 N 天的广告汇总指标（总花费、总点击、平均 ACOS、平均转化率等）")
    public Map<String, Object> getAdMetrics(String asin, int days) {
        log.info("[Mock] getAdMetrics: asin={}, days={}", asin, days);
        return Map.of(
                "asin", asin,
                "totalSpend", 97.3,
                "totalClicks", 330,
                "totalImpressions", 23500,
                "totalOrders", 23,
                "avgAcos", 0.20,
                "avgCtr", 0.014,
                "avgCvr", 0.070,
                "period", days + " days"
        );
    }

    @MasTool("获取指定广告活动的关键词表现报告（关键词、匹配类型、BID、点击、花费、转化等）")
    public Map<String, Object> getKeywordReport(String asin, String campaignId, int days) {
        log.info("[Mock] getKeywordReport: asin={}, campaignId={}, days={}", asin, campaignId, days);
        return Map.of(
                "campaignId", campaignId,
                "keywords", java.util.List.of(
                        Map.of("keywordId", "KW001", "keyword", "running shoes", "matchType", "BROAD",
                                "bid", 1.2, "clicks", 45, "spend", 12.5, "orders", 4, "acos", 0.15),
                        Map.of("keywordId", "KW002", "keyword", "sports shoes men", "matchType", "PHRASE",
                                "bid", 0.8, "clicks", 28, "spend", 8.3, "orders", 2, "acos", 0.25),
                        Map.of("keywordId", "KW003", "keyword", "cheap sneakers", "matchType", "EXACT",
                                "bid", 0.5, "clicks", 15, "spend", 4.2, "orders", 0, "acos", 0.0)
                ),
                "period", days + " days"
        );
    }

    @MasTool("获取指定 ASIN 的竞品广告和价格数据（竞品 ASIN、价格、评分、广告位等）")
    public Map<String, Object> getCompetitorData(String asin) {
        log.info("[Mock] getCompetitorData: asin={}", asin);
        return Map.of(
                "asin", asin,
                "competitors", java.util.List.of(
                        Map.of("competitorAsin", "B00COMP01", "price", 29.99, "rating", 4.3,
                                "reviewCount", 1200, "adPosition", "TOP_OF_SEARCH"),
                        Map.of("competitorAsin", "B00COMP02", "price", 24.99, "rating", 4.1,
                                "reviewCount", 800, "adPosition", "REST_OF_SEARCH")
                )
        );
    }
}
