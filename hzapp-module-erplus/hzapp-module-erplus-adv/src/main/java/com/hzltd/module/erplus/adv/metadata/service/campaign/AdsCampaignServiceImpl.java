package com.hzltd.module.erplus.adv.metadata.service.campaign;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.adapter.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignScheduleDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignScheduleMapper;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignUpdateReqVO;
import com.hzltd.framework.common.util.object.BeanUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

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
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_001, "广告计划不存在"));
        }

        // 2. 本地保存：将 VO 转为 DO 并更新数据库（包含 extData、deliverySchedule 等所有字段）
        AdsCampaignDO updateObj = BeanUtils.toBean(updateReqVO, AdsCampaignDO.class);
        adsCampaignMapper.updateById(updateObj);

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
        if (scheduleJson == null || scheduleJson.trim().isEmpty()) {
            AdsCampaignScheduleDO existing = adsCampaignScheduleMapper.selectByCampaignId(campaignId);
            if (existing != null) {
                adsCampaignScheduleMapper.deleteById(existing.getId());
            }
            return;
        }

        AdsCampaignDO campaign = adsCampaignMapper.selectById(campaignId);
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
        scheduleDO.setCurrentStatus(transition.getRequiredEnabled() ? "ENABLED" : "PAUSED");
        scheduleDO.setNextTransitionTime(transition.getNextTime());
        
        if (scheduleDO.getId() == null) {
            adsCampaignScheduleMapper.insert(scheduleDO);
        } else {
            adsCampaignScheduleMapper.updateById(scheduleDO);
        }
        
        if (!transition.getRequiredEnabled().equals("ENABLED".equals(campaign.getStatus()))) {
            log.info("[calculateAndSaveNextTransition] 调度立即生效: campaignId={}, status={}", 
                campaignId, transition.getRequiredEnabled() ? "ENABLED" : "PAUSED");
            updateCampaignStatus(campaignId, transition.getRequiredEnabled() ? "ENABLED" : "PAUSED");
        }
    }

    public ScheduleTransition calculateNextTransition(String scheduleJson, String timezoneStr) {
        try {
            boolean[][] grid = objectMapper.readValue(scheduleJson, new TypeReference<boolean[][]>() {});
            if (grid.length != 7) return null;

            ZoneId zoneId = ZoneId.of(timezoneStr != null ? timezoneStr : "UTC");
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
                    // 重要：这里将广告账户当地的变迁时间点转换为 UTC 时间存储，
                    // 确保 Quartz 任务在物理时间点对齐触发，不受服务器时区影响。
                    return new ScheduleTransition(currentTargetEnabled, next.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
                }
            }
            // 如果 7x24 全部相同，则下一次切换时间设为很久以后，同样转为 UTC
            return new ScheduleTransition(currentTargetEnabled, nowInZone.plusYears(10).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        } catch (Exception e) {
            log.error("Failed to calculate next transition for schedule: {}", scheduleJson, e);
            return null;
        }
    }

    @lombok.Value
    public static class ScheduleTransition {
        Boolean requiredEnabled;
        LocalDateTime nextTime;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampaignStatus(Long id, String status) {
        AdsCampaignDO campaign = adsCampaignMapper.selectById(id);
        if (campaign == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_001, "广告计划不存在"));
        }

        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        if (account != null && account.getCredentialId() != null) {
            AdsAccountCredentialDO credential = adsAccountCredentialMapper.selectById(account.getCredentialId());
            if (credential != null && campaign.getExternalId() != null) {
                try {
                    AdsPlatformAdapter adapter = adsPlatformAdapterFactory
                        .getAdapter(com.hzltd.module.erplus.adv.enums.AdsPlatformEnum.valueOf(account.getPlatform()));
                    
                    AdsStatusUpdateRequest updateReq = new AdsStatusUpdateRequest();
                    updateReq.setCampaignId(campaign.getExternalId());
                    updateReq.setStatus(status);
                    
                    Boolean success = adapter.updateStatus(account.getId(), updateReq);
                    if (Boolean.TRUE.equals(success)) {
                        log.info("[updateCampaignStatus] 平台状态同步成功: campaignId={}, status={}", id, status);
                    } else {
                        log.warn("[updateCampaignStatus] 平台状态同步失败: campaignId={}, status={}", id, status);
                    }
                } catch (Exception e) {
                    log.warn("[updateCampaignStatus] 平台状态同步异常: campaignId={}", id, e);
                }
            }
        }

        AdsCampaignDO updateObj = new AdsCampaignDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        adsCampaignMapper.updateById(updateObj);
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

}
