package com.hzltd.module.erplus.adv.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignScheduleDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignScheduleMapper;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 广告计划分时投放 Quartz JobHandler
 */
@Slf4j
@Component
public class AdsCampaignDeliveryJob implements JobHandler {

    @Resource
    private AdsCampaignScheduleMapper adsCampaignScheduleMapper;

    @Resource
    private AdsCampaignService adsCampaignService;

    @Resource
    private AdsCampaignMapper adsCampaignMapper;

    @Resource
    private AdsCampaignDeliveryJob self;

    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        log.info("[AdsCampaignDeliveryJob] 开始执行分时投放状态检查...");
        // 使用 UTC 时间进行检索
        
        // 1. 获取所有达到变迁时间点的记录
        List<AdsCampaignScheduleDO> schedules = adsCampaignScheduleMapper.selectListByNextTransitionTime(System.currentTimeMillis());
        if (schedules.isEmpty()) {
            log.info("[AdsCampaignDeliveryJob] 无需执行变迁的广告计划");
            return "无任务";
        }

        log.info("[AdsCampaignDeliveryJob] 发现 {} 个计划需要执行状态变迁", schedules.size());

        for (AdsCampaignScheduleDO schedule : schedules) {
            try {
                processTransition(schedule);
            } catch (Exception e) {
                log.error("[AdsCampaignDeliveryJob] 执行计划变迁失败: campaignId={}", schedule.getCampaignId(), e);
            }
        }
        return "执行完成: " + schedules.size();
    }

    private void processTransition(AdsCampaignScheduleDO schedule) {
        Long campaignId = schedule.getCampaignId();
        AdsCampaignDO campaign = adsCampaignMapper.selectById(campaignId);
        if (campaign == null) {
            adsCampaignScheduleMapper.deleteById(schedule.getId());
            return;
        }

        // 直接调用 Service 的计算方法。该方法会：
        // 1. 重算“现在”应有的状态并按需执行 API 同步。
        // 2. 自动计算下一个“变迁点”并更新到数据库（以 UTC 存储）。
        adsCampaignService.calculateAndSaveNextTransition(campaignId, campaign.getDeliverySchedule());
    }
}
