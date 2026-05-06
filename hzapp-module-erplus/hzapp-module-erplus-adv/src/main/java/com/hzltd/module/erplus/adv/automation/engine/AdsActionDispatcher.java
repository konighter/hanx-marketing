package com.hzltd.module.erplus.adv.automation.engine;

import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationLogDO;
import com.hzltd.module.erplus.adv.dal.mysql.automation.AdsAutomationLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 广告操作分发器
 * 处理具体的 Amazon/Meta API 原子操作并记录日志
 *
 * @author antigravity
 */
@Slf4j
@Service
public class AdsActionDispatcher {

    @Resource
    private AdsAutomationLogMapper adsAutomationLogMapper;

    /**
     * 关键词转移 (从自动到手动精准)
     */
    public void promoteKeyword(Long planId, String term, Long sourceCampaignId, Long targetCampaignId, String ruleName, Map<String, Object> metrics) {
        log.info("Promoting keyword: '{}' from campaign {} to {}", term, sourceCampaignId, targetCampaignId);
        
        // TODO: 调用 Amazon SP-API 在目标广告组新增关键词
        // TODO: 调用 Amazon SP-API 在源广告活动添加否定关键词
        
        logAction(planId, ruleName, metrics, Map.of(
            "action", "PROMOTE_KEYWORD",
            "term", term,
            "sourceCampaignId", sourceCampaignId,
            "targetCampaignId", targetCampaignId
        ));
    }

    /**
     * 调整出价
     */
    public void adjustBid(Long planId, Long keywordId, Double currentBid, Double newBid, String ruleName, Map<String, Object> metrics) {
        log.info("Adjusting bid for keyword {}: {} -> {}", keywordId, currentBid, newBid);
        
        // TODO: 调用 Amazon SP-API 更新出价
        
        logAction(planId, ruleName, metrics, Map.of(
            "action", "ADJUST_BID",
            "keywordId", keywordId,
            "oldBid", currentBid,
            "newBid", newBid
        ));
    }

    /**
     * 调整预算
     */
    public void adjustBudget(Long planId, Long campaignId, Double currentBudget, Double newBudget, String ruleName, Map<String, Object> metrics) {
        log.info("Adjusting budget for campaign {}: {} -> {}", campaignId, currentBudget, newBudget);
        
        // TODO: 调用 Amazon SP-API 更新预算
        
        logAction(planId, ruleName, metrics, Map.of(
            "action", "ADJUST_BUDGET",
            "campaignId", campaignId,
            "oldBudget", currentBudget,
            "newBudget", newBudget
        ));
    }

    /**
     * 状态变更 (暂停/启动)
     */
    public void updateState(Long planId, String type, Long id, String state, String ruleName, Map<String, Object> metrics) {
        log.info("Updating state for {} {}: -> {}", type, id, state);
        
        // TODO: 调用 Amazon SP-API 更新状态
        
        logAction(planId, ruleName, metrics, Map.of(
            "action", "UPDATE_STATE",
            "type", type,
            "id", id,
            "state", state
        ));
    }

    /**
     * 记录自动化日志
     */
    private void logAction(Long planId, String ruleName, Object triggerData, Object actionTaken) {
        AdsAutomationLogDO logDO = AdsAutomationLogDO.builder()
                .planId(planId)
                .ruleName(ruleName)
                .triggerData(triggerData)
                .actionTaken(actionTaken)
                .build();
        adsAutomationLogMapper.insert(logDO);
    }
}
