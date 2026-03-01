package com.hzltd.module.erplus.adv.metadata.service.task;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignScheduleDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignScheduleMapper;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 广告计划分时投放 Quartz JobHandler
 */
@Slf4j
@Component
public class AdsCampaignDeliveryJob implements JobHandler, ApplicationRunner {

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
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        
        // 1. 获取所有达到变迁时间点的记录
        List<AdsCampaignScheduleDO> schedules = adsCampaignScheduleMapper.selectListByNextTransitionTime(nowUtc);
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

    @Override
    public void run(ApplicationArguments args) {
        log.info("[AdsCampaignDeliveryJob] 系统启动，执行分时投放调度初始化...");
        try {
            self.bootstrapSchedules();
        } catch (Exception e) {
            log.error("[AdsCampaignDeliveryJob] 初始化分时投放调度失败", e);
        }
    }


    /**
     * 初始化扫描：为所有设置了分时计划但尚未加入调度表的广告计划生成调度记录
     */
    @TenantJob
    public void bootstrapSchedules() {
        List<AdsCampaignDO> campaigns = adsCampaignMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsCampaignDO>()
                .isNotNull(AdsCampaignDO::getDeliverySchedule)
                .ne(AdsCampaignDO::getDeliverySchedule, ""));
        
        log.info("[AdsCampaignDeliveryJob] 发现 {} 个配置了分时计划的广告活动，准备初始化调度表...", campaigns.size());
        
        for (AdsCampaignDO campaign : campaigns) {
            AdsCampaignScheduleDO existing = adsCampaignScheduleMapper.selectByCampaignId(campaign.getId());
            if (existing == null) {
                log.info("[AdsCampaignDeliveryJob] 正在为计划初始化调度记录: campaignId={}", campaign.getId());
                adsCampaignService.calculateAndSaveNextTransition(campaign.getId(), campaign.getDeliverySchedule());
            }
        }
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
