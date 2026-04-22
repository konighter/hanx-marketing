package com.hzltd.module.amz.adv.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.amz.api.adv.model.event.AmazonAdConvertMetric;
import com.hzltd.module.amz.api.adv.model.event.AmazonAdTrafficMetric;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamConvertDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamRawDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamRealtimeDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportStreamConvertMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportStreamRawMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportStreamRealtimeMapper;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
public class AdsAmazonReportStreamHandler {

    @Resource
    private AdsReportStreamRawMapper adsReportStreamRawMapper;

    @Resource
    private AdsReportStreamConvertMapper adsReportStreamConvertMapper;

    @Resource
    private AdsReportStreamRealtimeMapper adsReportStreamRealtimeMapper;

    @Resource
    private SystemShopService systemShopService;

    @TenantIgnore
    public void processStreamMessage(String sqsMessageBody) {
        try {
            JSONObject message = JSONUtil.parseObj(sqsMessageBody);

            String datasetId = message.getStr("dataset_id");

            if ("sp-traffic".equals(datasetId)) {
                this.processSPTrafficData(sqsMessageBody);
            } else {
                this.processSPConvertData(sqsMessageBody);
            }


        } catch (Exception e) {
            log.error("processStreamMessage Error, message = {}", sqsMessageBody, e);
        }


    }

    private void processSPTrafficData(String sqsMessageBody) {
        TenantUtils.executeIgnore(() -> {
            log.info("[processSPTrafficData] message = {}", sqsMessageBody);
            AmazonAdTrafficMetric trafficMetric = JsonUtils.parseObject(sqsMessageBody, AmazonAdTrafficMetric.class);
            // 写入本地 MySQL 原始表
            adsReportStreamRawMapper.insert(BeanUtils.toBean(trafficMetric, AdsReportStreamRawDO.class));

            // 双写至 Doris 统一实时表
            this.writeToDorisRealtime(trafficMetric);
        });

    }

    private void writeToDorisRealtime(AmazonAdTrafficMetric metric) {
        try {
            ShopModel shop = systemShopService.getShopBySellerIdAndMarketplaceIdWithoutTenant(metric.getAdvertiserId(), metric.getMarketplaceId());
            if (shop == null) {
                log.warn("[writeToDorisRealtime] 未找到店铺, advertiserId={}, marketplaceId={}", metric.getAdvertiserId(), metric.getMarketplaceId());
                return;
            }

            AdsReportStreamRealtimeDO realtimeDO = AdsReportStreamRealtimeDO.builder()
                    .windowStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(metric.getTimeWindowStart()), ZoneOffset.UTC))
                    .shopId(shop.getId())
                    .platform("AMAZON")
                    .campaignId(metric.getCampaignId())
                    .adGroupId(metric.getAdGroupId())
                    .adId(metric.getAdId())
                    .keywordId(metric.getKeywordId())
                    .placement(metric.getPlacement() == null ? "" : metric.getPlacement())
                    .productAsin("") // 实时流暂无 ASIN
                    .impressions(metric.getImpressions() != null ? metric.getImpressions().longValue() : 0L)
                    .clicks(metric.getClicks() != null ? metric.getClicks().longValue() : 0L)
                    .cost(metric.getCost() != null ? metric.getCost() : BigDecimal.ZERO)
                    .build();
            
            adsReportStreamRealtimeMapper.insert(realtimeDO);
        } catch (Exception e) {
            log.error("[writeToDorisRealtime] Traffic 双写 Doris 失败", e);
        }
    }

    private void processSPConvertData(String sqsMessageBody) {
        TenantUtils.executeIgnore(() -> {
            log.info("[processSPConvertData] message = {}", sqsMessageBody);
            AmazonAdConvertMetric convertMetric = JsonUtils.parseObject(sqsMessageBody, AmazonAdConvertMetric.class);
            // 写入本地 MySQL 转化表
            adsReportStreamConvertMapper.insert(BeanUtils.toBean(convertMetric, AdsReportStreamConvertDO.class));

            // 双写至 Doris 统一实时表
            this.writeToDorisRealtime(convertMetric);
        });
    }

    private void writeToDorisRealtime(AmazonAdConvertMetric metric) {
        try {
            ShopModel shop = systemShopService.getShopBySellerIdAndMarketplaceIdWithoutTenant(metric.getAdvertiserId(), metric.getMarketplaceId());
            if (shop == null) {
                log.warn("[writeToDorisRealtime] 未找到店铺, advertiserId={}, marketplaceId={}", metric.getAdvertiserId(), metric.getMarketplaceId());
                return;
            }

            AdsReportStreamRealtimeDO realtimeDO = AdsReportStreamRealtimeDO.builder()
                    .windowStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(metric.getTimeWindowStart()), ZoneOffset.UTC))
                    .shopId(shop.getId())
                    .platform("AMAZON")
                    .campaignId(metric.getCampaignId())
                    .adGroupId(metric.getAdGroupId())
                    .adId(metric.getAdId())
                    .keywordId(metric.getKeywordId())
                    .placement(metric.getPlacement() == null ? "" : metric.getPlacement())
                    .productAsin("")
                    // 通用指标映射：按约定使用 7D 归因
                    .sales(metric.getAttributedSales7d() != null ? BigDecimal.valueOf(metric.getAttributedSales7d()) : BigDecimal.ZERO)
                    .orders(metric.getAttributedUnitsOrdered7d() != null ? metric.getAttributedUnitsOrdered7d() : 0L)
                    // 详细归因指标
                    .amzAttributedSales1d(metric.getAttributedSales1d() != null ? BigDecimal.valueOf(metric.getAttributedSales1d()) : BigDecimal.ZERO)
                    .amzAttributedSales7d(metric.getAttributedSales7d() != null ? BigDecimal.valueOf(metric.getAttributedSales7d()) : BigDecimal.ZERO)
                    .amzAttributedSales14d(metric.getAttributedSales14d() != null ? BigDecimal.valueOf(metric.getAttributedSales14d()) : BigDecimal.ZERO)
                    .amzAttributedSales30d(metric.getAttributedSales30d() != null ? BigDecimal.valueOf(metric.getAttributedSales30d()) : BigDecimal.ZERO)
                    .amzAttributedUnitsOrdered1d(metric.getAttributedUnitsOrdered1d())
                    .amzAttributedUnitsOrdered7d(metric.getAttributedUnitsOrdered7d())
                    .amzAttributedUnitsOrdered14d(metric.getAttributedUnitsOrdered14d())
                    .amzAttributedUnitsOrdered30d(metric.getAttributedUnitsOrdered30d())
                    .amzAttributedSales1dSameSku(metric.getAttributedSales1dSameSku() != null ? BigDecimal.valueOf(metric.getAttributedSales1dSameSku()) : BigDecimal.ZERO)
                    .amzAttributedSales7dSameSku(metric.getAttributedSales7dSameSku() != null ? BigDecimal.valueOf(metric.getAttributedSales7dSameSku()) : BigDecimal.ZERO)
                    .amzAttributedSales14dSameSku(metric.getAttributedSales14dSameSku() != null ? BigDecimal.valueOf(metric.getAttributedSales14dSameSku()) : BigDecimal.ZERO)
                    .amzAttributedSales30dSameSku(metric.getAttributedSales30dSameSku() != null ? BigDecimal.valueOf(metric.getAttributedSales30dSameSku()) : BigDecimal.ZERO)
                    .amzAttributedUnitsOrdered1dSameSku(metric.getAttributedUnitsOrdered1dSameSku())
                    .amzAttributedUnitsOrdered7dSameSku(metric.getAttributedUnitsOrdered7dSameSku())
                    .amzAttributedUnitsOrdered14dSameSku(metric.getAttributedUnitsOrdered14dSameSku())
                    .amzAttributedUnitsOrdered30dSameSku(metric.getAttributedUnitsOrdered30dSameSku())
                    .build();

            adsReportStreamRealtimeMapper.insert(realtimeDO);
        } catch (Exception e) {
            log.error("[writeToDorisRealtime] Convert 双写 Doris 失败", e);
        }
    }


}

