package com.hzltd.module.erplus.adv.metadata.service.campaign;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.adv.model.AdsBudgetUpdateRequest;
import com.hzltd.module.adv.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignScheduleDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignScheduleMapper;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupUpdateReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignUpdateReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.adv.enums.AdsStatusEnum.*;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.*;


/**
 * 广告计划 Service 实现类
 */
@Service
@Validated
@Slf4j
public class AdsCampaignServiceImpl implements AdsCampaignService {

    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsAdGroupMapper adsAdGroupMapper;
    @Resource
    private AdsAccountCredentialMapper adsAccountCredentialMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;
    @Resource
    private AdsCampaignScheduleMapper adsCampaignScheduleMapper;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 广告计划状态流转规则
     */
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            ENABLED.getCode(), Set.of(PAUSED.getCode(), STOPPED.getCode()),
            PAUSED.getCode(), Set.of(ENABLED.getCode(), STOPPED.getCode()),
            STOPPED.getCode(), Set.of(ENABLED.getCode(), ARCHIVED.getCode())
    );

    @Override
    public PageResult<AdsCampaignDO> getCampaignPage(AdsCampaignPageReqVO pageReqVO) {
        return adsCampaignMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampaign(AdsCampaignUpdateReqVO updateReqVO) {
        // 1. 校验存在
        AdsCampaignDO campaign = adsCampaignMapper.selectById(updateReqVO.getId());
        if (campaign == null) {
            throw exception(ADS_CAMPAIGN_NOT_EXISTS);
        }

        // 2. 本地保存：将 VO 转为 DO 并更新数据库（包含 extData、deliverySchedule 等所有字段）
        AdsCampaignDO updateObj = BeanUtils.toBean(updateReqVO, AdsCampaignDO.class);
        adsCampaignMapper.updateById(updateObj);

        // 2.1 同步更新子广告组本地数据
        if (!CollectionUtils.isEmpty(updateReqVO.getAdGroups())) {
            for (AdsAdGroupUpdateReqVO adGroupVO : updateReqVO.getAdGroups()) {
                AdsAdGroupDO adGroupDO = BeanUtils.toBean(adGroupVO, AdsAdGroupDO.class);
                adsAdGroupMapper.updateById(adGroupDO);
            }
        }

        // 3. 平台同步 Hook：根据账户平台类型，调用对应适配器的 postCampaignUpdate
        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsCampaignDO updatedCampaign = adsCampaignMapper.selectById(updateReqVO.getId());
                adapter.postCampaignUpdate(account, updatedCampaign, updateReqVO.getExtData());
            } catch (UnsupportedOperationException e) {
                log.debug("[updateCampaign] 平台 {} 暂不支持 postCampaignUpdate hook", account.getPlatform());
            } catch (Exception e) {
                log.warn("[updateCampaign] 平台同步 hook 执行异常, campaignId={}, platform={}",
                        updateReqVO.getId(), account.getPlatform(), e);
            }
        }
        // 4. 重算并更新分时调度计划
        calculateAndSaveNextTransition(updateReqVO.getId(), updateObj.getDeliverySchedule());
    }

    @Override
    public void calculateAndSaveNextTransition(Long campaignId, String scheduleJson) {
        calculateAndSaveNextTransition(campaignId, scheduleJson, true);
    }

    @Override
    public void calculateAndSaveNextTransition(Long campaignId, String scheduleJson, boolean reconcile) {
        AdsCampaignDO campaign = adsCampaignMapper.selectById(campaignId);
        if (campaign == null) return;
        
        // 停用或归档状态下不进行分时调度变更，并清理现有调度
        if (STOPPED.getCode().equals(campaign.getStatus()) || ARCHIVED.getCode().equals(campaign.getStatus())) {
            AdsCampaignScheduleDO existing = adsCampaignScheduleMapper.selectByCampaignId(campaignId);
            if (existing != null) {
                adsCampaignScheduleMapper.deleteById(existing.getId());
            }
            return;
        }

        if (scheduleJson == null || scheduleJson.trim().isEmpty()) {
            AdsCampaignScheduleDO existing = adsCampaignScheduleMapper.selectByCampaignId(campaignId);
            if (existing != null) {
                adsCampaignScheduleMapper.deleteById(existing.getId());
            }
            return;
        }

        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        String timezone = account != null ? account.getTimezone() : "UTC";

        ScheduleTransition transition = calculateNextTransition(scheduleJson, timezone);
        if (transition == null) return;

        AdsCampaignScheduleDO scheduleDO = adsCampaignScheduleMapper.selectByCampaignId(campaignId);
        if (scheduleDO == null) {
            scheduleDO = AdsCampaignScheduleDO.builder()
                    .campaignId(campaignId)
                    .accountId(campaign.getAccountId())
                    .build();
        }
        scheduleDO.setCurrentStatus(transition.getRequiredEnabled() ? ENABLED.getCode() : PAUSED.getCode());
        scheduleDO.setNextTransitionTimestamp(transition.getNextTime());
        
        // 兼容性：同时设置旧的 LocalDateTime 字段 (统一使用 UTC)
        scheduleDO.setNextTransitionTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(transition.getNextTime()), ZoneOffset.UTC));
        
        if (scheduleDO.getId() == null) {
            adsCampaignScheduleMapper.insert(scheduleDO);
        } else {
            adsCampaignScheduleMapper.updateById(scheduleDO);
        }
        
        // 只有在 reconcile 为 true 时，才根据调度立即校准状态
        if (reconcile) {
            String requiredStatus = transition.getRequiredEnabled() ? ENABLED.getCode() : PAUSED.getCode();
            if (!requiredStatus.equals(campaign.getStatus())) {
                log.info("[calculateAndSaveNextTransition] 调度立即生效: campaignId={}, status={}", 
                    campaignId, requiredStatus);
                updateCampaignStatus(campaignId, requiredStatus);
            }
        }
    }

    public ScheduleTransition calculateNextTransition(String scheduleJson, String timezoneStr) {
        try {
            boolean[][] grid = objectMapper.readValue(scheduleJson, new TypeReference<boolean[][]>() {});
            if (grid.length != 7) return null;

            ZoneId zoneId = ZoneId.of(StringUtils.isNotEmpty(timezoneStr) ? timezoneStr : "UTC");
            ZonedDateTime nowInZone = ZonedDateTime.now(zoneId);
            
            int currentDayIdx = nowInZone.getDayOfWeek().getValue() - 1; 
            int currentHour = nowInZone.getHour();
            boolean currentTargetEnabled = grid[currentDayIdx][currentHour];
            
            // 寻找下一次变迁
            ZonedDateTime checkTime = nowInZone.withMinute(0).withSecond(0).withNano(0);
            for (int i = 1; i <= 7 * 24; i++) {
                ZonedDateTime next = checkTime.plusHours(i);
                int dayIdx = next.getDayOfWeek().getValue() - 1;
                int hour = next.getHour();
                if (grid[dayIdx][hour] != currentTargetEnabled) {
                    // 重要：这里将广告账户当地的变迁时间点转换为 Epoch 毫秒时间戳存储
                    return new ScheduleTransition(currentTargetEnabled, next.toInstant().toEpochMilli());
                }
            }
            // 如果 7x24 全部相同，则下一次切换时间设为很久以后
            return new ScheduleTransition(currentTargetEnabled, nowInZone.plusYears(10).toInstant().toEpochMilli());
        } catch (Exception e) {
            log.error("Failed to calculate next transition for schedule: {}", scheduleJson, e);
            return null;
        }
    }

    @lombok.Value
    public static class ScheduleTransition {
        Boolean requiredEnabled;
        Long nextTime;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampaignStatus(Long id, String status) {
        AdsCampaignDO campaign = adsCampaignMapper.selectById(id);
        if (campaign == null) {
            throw exception(ADS_CAMPAIGN_NOT_EXISTS);
        }

        String oldStatus = campaign.getStatus();
        // 1. 校验流转逻辑
        if (StringUtils.isNotEmpty(oldStatus) && !oldStatus.equals(status)) {
            Set<String> allowed = ALLOWED_TRANSITIONS.get(oldStatus);
            if (allowed == null || !allowed.contains(status)) {
                throw exception(ADS_CAMPAIGN_STATUS_TRANSITION_INVALID, oldStatus, status);
            }
        }

        // 2. 停用状态特殊处理
        if (STOPPED.getCode().equals(status)) {
            // 2.1 本地停用：不进行平台同步，仅更新本地状态
            updateCampaignStatusLocal(id, status);
            
            // 2.2 删除调度任务
            adsCampaignScheduleMapper.deleteByCampaignId(id);
            log.info("[updateCampaignStatus] 广告计划已在本地停用，删除调度任务: campaignId={}", id);
            return;
        }

        // 3. 平台同步
        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsStatusUpdateRequest updateReq = new AdsStatusUpdateRequest();
                updateReq.setEntityType(AdsEntityTypeEnum.CAMPAIGN);
                updateReq.setEntityId(campaign.getExternalId());
                updateReq.setLocalId(id);
                updateReq.setStatus(status);
                
                boolean success = adapter.updateStatus(account.getId(), updateReq);
                if (success) {
                    updateCampaignStatusLocal(id, status);

                    // 1. 如果新状态是 ENABLED，触发计算。
                    // 注意：这里使用 reconcile=false (策略A1)，即手动操作优先。
                    // 仅重算下一次切换点，不立即强制将状态改回网格预定状态，直到下一个调度点到来。
                    if (ENABLED.getCode().equals(status)) {
                        calculateAndSaveNextTransition(id, campaign.getDeliverySchedule(), false);
                    }
                    
                    // 2. 如果新状态是 ARCHIVED，由于是终态，清理掉该计划的所有调度记录
                    if (ARCHIVED.getCode().equals(status)) {
                        adsCampaignScheduleMapper.deleteByCampaignId(id);
                    }
                }
            } catch (Exception e) {
                log.warn("[updateCampaignStatus] 平台状态同步异常: campaignId={}, platform={}", id, account.getPlatform(), e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampaignBudget(Long id, java.math.BigDecimal budget) {
        AdsCampaignDO campaign = adsCampaignMapper.selectById(id);
        if (campaign == null) {
            throw exception(ADS_CAMPAIGN_NOT_EXISTS);
        }

        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsBudgetUpdateRequest updateReq = new AdsBudgetUpdateRequest();
                updateReq.setEntityType(AdsEntityTypeEnum.CAMPAIGN);
                updateReq.setEntityId(campaign.getExternalId());
                updateReq.setLocalId(id);
                updateReq.setBudget(budget);
                
                boolean success = adapter.updateBudget(account.getId(), updateReq);
                if (success) {
                    updateCampaignBudgetLocal(id, budget);
                }
            } catch (Exception e) {
                log.warn("[updateCampaignBudget] 平台预算同步异常: campaignId={}, platform={}", id, account.getPlatform(), e);
            }
        }


    }

    @Override
    public AdsCampaignDO getCampaign(Long id) {
        return adsCampaignMapper.selectById(id);
    }

    @Override
    public String getPlatformByAccountId(Long accountId) {
        AdsAccountDO account = adsAccountMapper.selectById(accountId);
        return account != null ? account.getPlatform() : null;
    }

    private void updateCampaignStatusLocal(Long id, String status) {
        AdsCampaignDO updateObj = new AdsCampaignDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        adsCampaignMapper.updateById(updateObj);
    }

    private void updateCampaignBudgetLocal(Long id, java.math.BigDecimal budget) {
        AdsCampaignDO updateObj = new AdsCampaignDO();
        updateObj.setId(id);
        updateObj.setDailyBudget(budget);
        adsCampaignMapper.updateById(updateObj);
    }

}
