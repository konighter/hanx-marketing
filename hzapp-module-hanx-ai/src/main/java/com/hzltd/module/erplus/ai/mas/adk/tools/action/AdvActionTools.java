package com.hzltd.module.erplus.ai.mas.adk.tools.action;

import com.hzltd.module.erplus.ai.mas.agent.MasTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 广告操作类 Tools (Mock 实现)
 * 提供广告活动启停、BID 调整、关键词管理等操作能力
 */
@Slf4j
@Component("advActionTools")
public class AdvActionTools {

    @MasTool("启用或暂停指定的广告活动。enabled=true 表示启用，false 表示暂停")
    public Map<String, Object> toggleCampaignStatus(String campaignId, boolean enabled) {
        log.info("[Mock] toggleCampaignStatus: campaignId={}, enabled={}", campaignId, enabled);
        return Map.of(
                "success", true,
                "campaignId", campaignId,
                "newStatus", enabled ? "ENABLED" : "PAUSED",
                "message", "广告活动状态已更新为: " + (enabled ? "启用" : "暂停")
        );
    }

    @MasTool("调整指定广告组中目标的 BID 竞价（单位: 美元）")
    public Map<String, Object> adjustBid(String campaignId, String adGroupId, String targetId, double newBid) {
        log.info("[Mock] adjustBid: campaignId={}, adGroupId={}, targetId={}, newBid={}",
                campaignId, adGroupId, targetId, newBid);
        return Map.of(
                "success", true,
                "targetId", targetId,
                "previousBid", 1.0,
                "newBid", newBid,
                "message", "BID 已从 $1.00 调整为 $" + String.format("%.2f", newBid)
        );
    }

    @MasTool("调整广告活动的位置 BID 规则（placement: TOP_OF_SEARCH / REST_OF_SEARCH / PRODUCT_PAGE, percentage: 调整百分比 0-900）")
    public Map<String, Object> adjustPlacementBid(String campaignId, String placement, int percentage) {
        log.info("[Mock] adjustPlacementBid: campaignId={}, placement={}, percentage={}",
                campaignId, placement, percentage);
        return Map.of(
                "success", true,
                "campaignId", campaignId,
                "placement", placement,
                "previousPercentage", 50,
                "newPercentage", percentage,
                "message", placement + " 位置 BID 规则已调整为 " + percentage + "%"
        );
    }

    @MasTool("向指定广告组添加新的关键词（matchType: BROAD / PHRASE / EXACT, bid: 竞价金额）")
    public Map<String, Object> addKeyword(String campaignId, String adGroupId, String keyword,
                                           String matchType, double bid) {
        log.info("[Mock] addKeyword: campaignId={}, adGroupId={}, keyword={}, matchType={}, bid={}",
                campaignId, adGroupId, keyword, matchType, bid);
        return Map.of(
                "success", true,
                "keywordId", "KW_NEW_" + System.currentTimeMillis(),
                "keyword", keyword,
                "matchType", matchType,
                "bid", bid,
                "message", "关键词 '" + keyword + "' (" + matchType + ") 已添加, BID=$" + String.format("%.2f", bid)
        );
    }

    @MasTool("删除指定的关键词")
    public Map<String, Object> removeKeyword(String keywordId) {
        log.info("[Mock] removeKeyword: keywordId={}", keywordId);
        return Map.of(
                "success", true,
                "keywordId", keywordId,
                "message", "关键词 " + keywordId + " 已删除"
        );
    }

    @MasTool("调整指定关键词的 BID 竞价（单位: 美元）")
    public Map<String, Object> adjustKeywordBid(String keywordId, double newBid) {
        log.info("[Mock] adjustKeywordBid: keywordId={}, newBid={}", keywordId, newBid);
        return Map.of(
                "success", true,
                "keywordId", keywordId,
                "previousBid", 0.8,
                "newBid", newBid,
                "message", "关键词 BID 已从 $0.80 调整为 $" + String.format("%.2f", newBid)
        );
    }
}
