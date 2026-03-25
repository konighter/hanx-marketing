package com.hzltd.module.amz.api.adv.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.amz.dal.mapper.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsBudgetBurnRateDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsBudgetBurnRateMapper;
import com.hzltd.module.system.enums.NotifyChannelTypeEnum;
import com.hzltd.module.spapi.model.system.NotifyMessage;
import com.hzltd.module.system.service.ChannelNotifySendService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Amazon 广告预算 Stream 消息监听器
 * 
 * 监听 budget-queue, 处理预算相关的实时消息
 */
@Slf4j
@Service
public class AdsAmazonManagerListener extends AbstractAmazonSqsListener {

    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Resource
    private ChannelNotifySendService channelNotifySendService;

    @Resource
    private AdsBudgetBurnRateMapper adsBudgetBurnRateMapper;

    @SqsListener("${hzapp.aws.sqs.ads-manager-queue}")
    public void onMessage(String message) {
        processMessage(message);
    }

    @Override
    protected void handleBusinessMessage(String message) {
        log.info("[AdsAmazonManagerListener] 收到预算消息: {}", message);
        try {
            JSONObject jsonObject = JSONUtil.parseObj(message);
            String datasetId = jsonObject.getStr("dataset_id");

            if ("budget-usage".equals(datasetId)) {
                // 处理超预算预警
                AdsAmazonBudgetUsageRecord record = JSONUtil.toBean(jsonObject, AdsAmazonBudgetUsageRecord.class);
                processBudgetUsageRecord(record);
            } else {
                log.info("[AdsAmazonManagerListener] 收到其他 dataset_id 的消息: {}", datasetId);
            }
        } catch (Exception e) {
            log.error("[AdsAmazonManagerListener] 处理预算消息异常", e);
        }
    }
    
    private void processBudgetUsageRecord(AdsAmazonBudgetUsageRecord record) {
        String advertiserId = record.getAdvertiserId();
        String campaignId = record.getBudgetScopeId();
        Double budgetUsagePercentage = record.getBudgetUsagePercentage();
        String usageUpdatedTimestampStr = record.getUsageUpdatedTimestamp();

        if (advertiserId == null || budgetUsagePercentage == null || usageUpdatedTimestampStr == null) {
            return;
        }


        // 根据 advertiserId(对应通常是 profileId) 查找 Profile
        List<AdsAmazonProfileDO> profiles = adsAmazonProfileMapper.selectBySellerId(advertiserId);
        if (CollectionUtils.isEmpty(profiles)) {
            log.warn("[AdsAmazonManagerListener] 未找到 advertiserId={} 对应的 Profile", advertiserId);
            return;
        }
        // todo -- 这里找出来的profileId不准确, 时区这些可能对不上
        AdsAmazonProfileDO profile = profiles.get(0);

        ZoneId zoneId = ZoneId.of(profile.getTimezone() != null ? profile.getTimezone() : "UTC");
        ZonedDateTime updatedTime;
        Instant instant;
        try {
            instant = Instant.parse(usageUpdatedTimestampStr);
            updatedTime = instant.atZone(zoneId);
        } catch (Exception e) {
            log.warn("[AdsAmazonManagerListener] 解析时间戳失败: {}", usageUpdatedTimestampStr);
            return;
        }

        double previousPercentage = getPreviousPercentageForToday(campaignId, updatedTime, zoneId);

        try {
            AdsBudgetBurnRateDO burnRateDO = AdsBudgetBurnRateDO.builder()
                    .advertiserId(advertiserId)
                    .marketplaceId(record.getMarketplaceId())
                    .datasetId(record.getDatasetId())
                    .budgetScopeId(campaignId)
                    .budgetScopeType(record.getBudgetScopeType())
                    .advertisingProductType(record.getAdvertisingProductType())
                    .budget(record.getBudget())
                    .budgetUsagePercentage(budgetUsagePercentage)
                    .usageUpdatedTimestamp(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
                    .build();
            adsBudgetBurnRateMapper.insert(burnRateDO);
        } catch (Exception e) {
            log.error("[AdsAmazonManagerListener] 保存预算消耗记录失败", e);
        }
        
        // 计算当天的时间进度 (百分比)
        double secondsInDay = updatedTime.toLocalTime().toSecondOfDay();
        double timeProgressPercentage = (secondsInDay / 86400.0) * 100.0;

        log.info("[AdsAmazonManagerListener] campaignId={} 预算消耗: {}%, 时间进度: {}%", 
                campaignId, budgetUsagePercentage, timeProgressPercentage);

        // 如果 budget_usage_percentage 的比例 大于 usage_updated_timestamp在当天的时间进度 超过50%
        if (budgetUsagePercentage - timeProgressPercentage > 50.0) {
            sendBudgetAlertNotify(profile, campaignId, budgetUsagePercentage, timeProgressPercentage);
        }

        // 预算使用率在第一次是整十数时，发送预算使用通知
        int currentTens = (int) (budgetUsagePercentage / 10);
        int previousTens = (int) (previousPercentage / 10);
        if (currentTens > previousTens && currentTens > 0) {
            sendBudgetUsageNotify(profile, campaignId, budgetUsagePercentage, currentTens * 10);
        }
    }

    private void sendBudgetAlertNotify(AdsAmazonProfileDO profile, String campaignId, double usage, double progress) {
        NotifyMessage notifyMessage = new NotifyMessage();
        notifyMessage.setTitle("【超预算预警】亚马逊广告");
        notifyMessage.setLevel("warn");
        
        String content = String.format("广告账户 [%s] (%s) 的活动 (Campaign ID: %s) 预算消耗过快！\n" +
                "- 当前预算已使用: **%.2f%%**\n" +
                "- 当天时间进度仅: **%.2f%%**\n" +
                "- 当地时区: %s", 
                profile.getEntityName(), profile.getCountryCode(), campaignId, usage, progress, profile.getTimezone());
        notifyMessage.setContent(content);

        // 发送给启用的渠道
        channelNotifySendService.send(notifyMessage, NotifyChannelTypeEnum.FEISHU);
    }

    private void sendBudgetUsageNotify(AdsAmazonProfileDO profile, String campaignId, double usage, int threshold) {
        NotifyMessage notifyMessage = new NotifyMessage();
        notifyMessage.setTitle("【预算进度通知】亚马逊广告");
        notifyMessage.setLevel("info");
        
        String content = String.format("广告账户 [%s] (%s) 的活动 (Campaign ID: %s) 预算使用达到新阶段！\n" +
                "- 当前预算已使用: **%.2f%%**\n" +
                "- 触发里程碑: **%d%%**\n" +
                "- 当地时区: %s", 
                profile.getEntityName(), profile.getCountryCode(), campaignId, usage, threshold, profile.getTimezone());
        notifyMessage.setContent(content);

        // 发送给启用的渠道
        channelNotifySendService.send(notifyMessage, NotifyChannelTypeEnum.FEISHU);
    }

    @Override
    protected String getListenerName() {
        return "Budget";
    }

    private double getPreviousPercentageForToday(String campaignId, ZonedDateTime updatedTime, ZoneId zoneId) {
        try {
            LocalDateTime startOfDayInDb = updatedTime.toLocalDate().atStartOfDay(zoneId)
                    .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endOfDayInDb = updatedTime.toLocalDate().atTime(23, 59, 59)
                    .atZone(zoneId).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

            AdsBudgetBurnRateDO lastRecord = adsBudgetBurnRateMapper.selectOne(new LambdaQueryWrapperX<AdsBudgetBurnRateDO>()
                    .eq(AdsBudgetBurnRateDO::getBudgetScopeId, campaignId)
                    .between(AdsBudgetBurnRateDO::getUsageUpdatedTimestamp, startOfDayInDb, endOfDayInDb)
                    .orderByDesc(AdsBudgetBurnRateDO::getUsageUpdatedTimestamp)
                    .last("LIMIT 1"));

            if (lastRecord != null && lastRecord.getBudgetUsagePercentage() != null) {
                return lastRecord.getBudgetUsagePercentage();
            }
        } catch (Exception e) {
            log.error("[AdsAmazonManagerListener] 查询上一条记录失败", e);
        }
        return 0.0;
    }

    @Data
    public static class AdsAmazonBudgetUsageRecord {

        @JsonProperty("advertiser_id")
        private String advertiserId;

        @JsonProperty("marketplace_id")
        private String marketplaceId;

        @JsonProperty("dataset_id")
        private String datasetId;

        @JsonProperty("budget_scope_id")
        private String budgetScopeId;

        @JsonProperty("budget_scope_type")
        private String budgetScopeType;

        @JsonProperty("advertising_product_type")
        private String advertisingProductType;

        @JsonProperty("budget")
        private Double budget;

        @JsonProperty("budget_usage_percentage")
        private Double budgetUsagePercentage;

        @JsonProperty("usage_updated_timestamp")
        private String usageUpdatedTimestamp;
    }
}
