package com.hzltd.module.amz.adv.service;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.module.adv.model.AdsResponse;
import com.hzltd.module.system.enums.AdsPlatformEnum;
import com.hzltd.module.amz.api.adv.v1.AmazonAdsReportApiService;
import com.hzltd.module.amz.api.adv.v1.AmzSpReportConstants;
import com.hzltd.module.amz.api.adv.v1.model.AmzReportRequest;
import com.hzltd.module.amz.api.adv.v1.model.AmzReportResponse;
import com.hzltd.module.amz.api.enums.AmazonRegionEnum;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.amz.dal.mapper.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.dal.dataobject.*;
import com.hzltd.module.erplus.adv.dal.mysql.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

/**
 * Amazon Sponsored Products (SP) 报表解析与任务调度器
 * 
 * 职责：
 * 1. 任务生命周期管理（父任务拆分、子任务汇总、状态推进）
 * 2. 对接 Amazon Reporting API V3 提交请求与轮询
 * 3. 报表数据解析与 DB 入库
 */
@Slf4j
@Component
public class AdsAmazonReportServiceImpl implements AdsAmazonReportService {

    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAdGroupMapper adsAdGroupMapper;
    @Resource
    private AdsAdMapper adsAdMapper;
    @Resource
    private AdsKeywordMapper adsKeywordMapper;
    @Resource
    private AdsReportDailyMapper adsReportDailyMapper;
    @Resource
    private AdsReportSummaryMapper adsReportSummaryMapper;
    @Resource
    private AdsSyncTaskMapper adsSyncTaskMapper;
    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;
    @Resource
    @Lazy
    private AdsAuthService adsAuthService;
    @Resource
    private AmazonAdsReportApiService reportApiService;

    /**
     * 执行同步任务状态机
     */
    @Override
    public void executeSyncTask(AdsSyncTaskDO task) {
        String taskType = task.getTaskType();
        log.info("[executeSyncTask][Amazon] taskId={}, type={}, status={}", task.getId(), taskType, task.getStatus());

        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(task.getAccountId());
        if (credential == null) {
            log.error("[executeSyncTask][Amazon] 找不到凭证: accountId={}", task.getAccountId());
            task.setStatus("FAILED");
            task.setErrorMessage("Missing credential");
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
            return;
        }

        if ("REPORT_DAILY".equals(taskType)) {
            handleParentTask(task, credential);
        } else if ("REPORT_DIMENSION".equals(taskType)) {
            handleChildTask(task, credential);
        }
    }







    /**
     * 解析单条 Stream detail 并按 group_column 拆分为多条入库记录
     */
    private List<AdsReportHourlyDO> parseStreamDetail(JSONObject detail) {
        String datasetId = detail.getStr("datasetId");
        String advertiserId = detail.getStr("advertiserId");
        String timeWindowStart = detail.getStr("timeWindowStart");

        // 解析时间窗口
        LocalDateTime reportHour = parseTimeWindowStart(timeWindowStart);

        // 查找广告账户
        AdsAmazonProfileDO profile = adsAmazonProfileMapper.selectOne(
                AdsAmazonProfileDO::getEntityId, advertiserId);
        if (profile == null) {
            log.warn("[parseStreamDetail] 未找到 advertiserId={} 对应的 Profile, 跳过", advertiserId);
            return Collections.emptyList();
        }
        Long accountId = profile.getAccountId();
        Long shopId = profile.getShopId();

        // 解析外部 ID
        String extCampaignId = detail.getStr("campaignId");
        String extAdGroupId = detail.getStr("adGroupId");
        String extAdId = detail.getStr("adId");
        String extKeywordId = detail.getStr("keywordId");

        // 查找内部 ID
        Long campaignId = resolveInternalCampaignId(shopId, extCampaignId);
        Long adGroupId = resolveInternalAdGroupId(shopId, extAdGroupId);
        Long adId = resolveInternalAdId(shopId, extAdId);
        Long keywordId = resolveInternalKeywordId(shopId, extKeywordId);

        if (campaignId == null) {
            log.warn("[parseStreamDetail] 无法解析 campaignId, extCampaignId={}, 跳过", extCampaignId);
            return Collections.emptyList();
        }

        // 解析指标
        boolean isTraffic = datasetId != null && datasetId.contains("traffic");
        Long impressions = isTraffic ? detail.getLong("impressions", 0L) : 0L;
        Long clicks = isTraffic ? detail.getLong("clicks", 0L) : 0L;
        BigDecimal cost = isTraffic ? detail.getBigDecimal("cost", BigDecimal.ZERO) : BigDecimal.ZERO;

        boolean isConversion = datasetId != null && datasetId.contains("conversion");
        BigDecimal sales7d = isConversion ? detail.getBigDecimal("attributedSales7d", BigDecimal.ZERO) : BigDecimal.ZERO;
        Long orders7d = isConversion ? detail.getLong("attributedUnitsOrdered7d", 0L) : 0L;
        BigDecimal sales14d = isConversion ? detail.getBigDecimal("attributedSales14d", BigDecimal.ZERO) : BigDecimal.ZERO;
        Long orders14d = isConversion ? detail.getLong("attributedUnitsOrdered14d", 0L) : 0L;
        BigDecimal sales30d = isConversion ? detail.getBigDecimal("attributedSales30d", BigDecimal.ZERO) : BigDecimal.ZERO;
        Long orders30d = isConversion ? detail.getLong("attributedUnitsOrdered30d", 0L) : 0L;

        // 按 group_column 拆分多条记录
        List<AdsReportHourlyDO> records = new ArrayList<>();

        // 1. campaign 维度: 只保留 campaignId，其他维度 = -1
        records.add(buildHourlyRecord(accountId, "campaign", reportHour,
                campaignId, -1L, -1L, -1L,
                impressions, clicks, cost, sales7d, orders7d, sales14d, orders14d, sales30d, orders30d));

        // 2. adGroup 维度: 保留 campaignId + adGroupId
        if (adGroupId != null && adGroupId > 0) {
            records.add(buildHourlyRecord(accountId, "adGroup", reportHour,
                    campaignId, adGroupId, -1L, -1L,
                    impressions, clicks, cost, sales7d, orders7d, sales14d, orders14d, sales30d, orders30d));
        }

        // 3. ad 维度: 保留 campaignId + adGroupId + adId
        if (adId != null && adId > 0) {
            records.add(buildHourlyRecord(accountId, "ad", reportHour,
                    campaignId, adGroupId != null ? adGroupId : -1L, adId, -1L,
                    impressions, clicks, cost, sales7d, orders7d, sales14d, orders14d, sales30d, orders30d));
        }

        // 4. keyword 维度: 保留 campaignId + adGroupId + keywordId
        if (keywordId != null && keywordId > 0) {
            records.add(buildHourlyRecord(accountId, "keyword", reportHour,
                    campaignId, adGroupId != null ? adGroupId : -1L, -1L, keywordId,
                    impressions, clicks, cost, sales7d, orders7d, sales14d, orders14d, sales30d, orders30d));
        }

        return records;
    }

    private AdsReportHourlyDO buildHourlyRecord(Long accountId, String groupColumn, LocalDateTime reportHour,
                                                 Long campaignId, Long adGroupId, Long adId, Long keywordId,
                                                 Long impressions, Long clicks, BigDecimal cost,
                                                 BigDecimal sales7d, Long orders7d,
                                                 BigDecimal sales14d, Long orders14d,
                                                 BigDecimal sales30d, Long orders30d) {
        return AdsReportHourlyDO.builder()
                .accountId(accountId)
                .groupColumn(groupColumn)
                .reportHour(reportHour)
                .campaignId(campaignId)
                .adGroupId(adGroupId)
                .adId(adId)
                .keywordId(keywordId)
                .impressions(impressions)
                .clicks(clicks)
                .cost(cost)
                .sales7d(sales7d)
                .orders7d(orders7d)
                .sales14d(sales14d)
                .orders14d(orders14d)
                .sales30d(sales30d)
                .orders30d(orders30d)
                .build();
    }

    private LocalDateTime parseTimeWindowStart(String timeWindowStart) {
        if (timeWindowStart == null || timeWindowStart.isEmpty()) {
            return LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        }
        try {
            // Amazon Stream 时间格式: ISO 8601, e.g. "2026-03-04T10:00:00Z"
            return OffsetDateTime.parse(timeWindowStart)
                    .toLocalDateTime();
        } catch (Exception e) {
            log.warn("[parseTimeWindowStart] 无法解析时间 '{}', 使用当前小时", timeWindowStart);
            return LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        }
    }

    private Long resolveInternalCampaignId(Long shopId, String externalCampaignId) {
        if (externalCampaignId == null || externalCampaignId.isEmpty()) return null;
        AdsCampaignDO campaign = adsCampaignMapper.selectByShopAndExternalId(shopId, externalCampaignId);
        return campaign != null ? campaign.getId() : null;
    }

    private Long resolveInternalAdGroupId(Long shopId, String externalAdGroupId) {
        if (externalAdGroupId == null || externalAdGroupId.isEmpty()) return null;
        AdsAdGroupDO adGroup = adsAdGroupMapper.selectByShopAndExternalId(shopId, externalAdGroupId);
        return adGroup != null ? adGroup.getId() : null;
    }

    private Long resolveInternalAdId(Long shopId, String externalAdId) {
        if (externalAdId == null || externalAdId.isEmpty()) return null;
        AdsAdDO ad = adsAdMapper.selectByShopAndExternalId(shopId, externalAdId);
        return ad != null ? ad.getId() : null;
    }

    private Long resolveInternalKeywordId(Long shopId, String externalKeywordId) {
        if (externalKeywordId == null || externalKeywordId.isEmpty()) return null;
        AdsKeywordDO keyword = adsKeywordMapper.selectByShopAndExternalId(shopId, externalKeywordId);
        return keyword != null ? keyword.getId() : null;
    }


    // ==================== 父任务处理 ====================

    private void handleParentTask(AdsSyncTaskDO task, AdsAccountCredentialDO credential) {
        if ("PENDING".equals(task.getStatus())) {
            splitIntoChildTasks(task);
        } else if ("RUNNING".equals(task.getStatus())) {
            resolveParentStatus(task);
        }
    }

    private void splitIntoChildTasks(AdsSyncTaskDO parentTask) {
        List<AdsAmazonProfileDO> profiles = adsAmazonProfileMapper.selectListByAccountId(parentTask.getAccountId());
        if (profiles.isEmpty()) {
            parentTask.setStatus("SUCCESS");
            parentTask.setErrorMessage("No profiles found");
            parentTask.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(parentTask);
            return;
        }

        int childCount = 0;
        for (AdsAmazonProfileDO profile : profiles) {
            String regionName = profile.getRegion();
            AmazonRegionEnum region = AmazonRegionEnum.valueOf(regionName);
            String baseUrl = "https://" + region.getAdsEndpoint();

            // 1. spCampaigns + adGroup
            addChildTask(parentTask, profile, baseUrl, AmzSpReportConstants.SP_CAMPAIGNS, "campaign,adGroup");
            // 2. spCampaigns + campaignPlacement
            addChildTask(parentTask, profile, baseUrl, AmzSpReportConstants.SP_CAMPAIGNS, "campaign,campaignPlacement");
            // 3. spTargeting + targeting
            addChildTask(parentTask, profile, baseUrl, AmzSpReportConstants.SP_TARGETING, "targeting");
            // 4. spSearchTerm + searchTerm
            addChildTask(parentTask, profile, baseUrl, AmzSpReportConstants.SP_SEARCH_TERM, "searchTerm");
            childCount += 4;
        }

        log.info("[splitIntoChildTasks] 父任务 {} 拆分为 {} 条子任务", parentTask.getId(), childCount);
        parentTask.setStatus("RUNNING");
        adsSyncTaskMapper.updateById(parentTask);
    }

    private void addChildTask(AdsSyncTaskDO parentTask, AdsAmazonProfileDO profile, String baseUrl, String reportType, String groupBy) {
        Map<String, Object> context = new HashMap<>();
        context.put("profileId", profile.getProfileId());
        context.put("reportType", reportType);
        context.put("groupBy", groupBy);
        context.put("baseUrl", baseUrl);
        context.put("region", profile.getRegion());

        // 根据 profile 时区计算预计可执行时间:
        // dateRangeEnd 在目标时区的次日 00:00 + 4h 缓冲，转为 epoch millis
        long scheduledAt = computeScheduledAt(parentTask.getDateRangeEnd(), profile.getTimezone());

        AdsSyncTaskDO child = AdsSyncTaskDO.builder()
            .accountId(parentTask.getAccountId())
            .parentTaskId(parentTask.getId())
            .platform(AdsPlatformEnum.AMAZON.name())
            .taskType("REPORT_DIMENSION")
            .status("PENDING")
            .dateRangeStart(parentTask.getDateRangeStart())
            .dateRangeEnd(parentTask.getDateRangeEnd())
            .scheduledAt(scheduledAt)
            .context(context)
            .retryCount(0)
            .maxRetries(3)
            .startedAt(LocalDateTime.now())
            .build();

        adsSyncTaskMapper.insert(child);
    }

    /**
     * 根据目标市场时区计算报表可执行时间 (epoch millis)
     *
     * 逻辑: dateRangeEnd 在目标时区的次日 04:00 (留 4h 数据延迟缓冲)
     * 例: dateRangeEnd=2026-03-02, timezone=America/Los_Angeles
     *     -> 2026-03-03 04:00 PST -> 转为 epoch millis
     */
    private long computeScheduledAt(LocalDate dateRangeEnd, String timezone) {
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timezone);
        } catch (Exception e) {
            log.warn("[computeScheduledAt] 无法解析时区 '{}', 使用 UTC", timezone);
            zoneId = ZoneId.of("UTC");
        }
        // 报表日期的次日凌晨 4:00 (目标时区)
        ZonedDateTime targetTime = dateRangeEnd.plusDays(1)
            .atTime(LocalTime.of(4, 0))
            .atZone(zoneId);
        return targetTime.toInstant().toEpochMilli();
    }

    private void resolveParentStatus(AdsSyncTaskDO parentTask) {
        List<AdsSyncTaskDO> children = adsSyncTaskMapper.selectListByParentTaskId(parentTask.getId());
        if (children.isEmpty()) return;

        long successCount = children.stream().filter(c -> "SUCCESS".equals(c.getStatus())).count();
        long failedCount = children.stream().filter(c -> "FAILED".equals(c.getStatus())).count();
        
        if (successCount + failedCount < children.size()) return;

        if (failedCount == 0) {
            parentTask.setStatus("SUCCESS");
        } else if (successCount > 0) {
            parentTask.setStatus("PARTIAL");
        } else {
            parentTask.setStatus("FAILED");
        }
        parentTask.setFinishedAt(LocalDateTime.now());
        parentTask.setErrorMessage("Total: " + children.size() + ", Success: " + successCount + ", Failed: " + failedCount);
        adsSyncTaskMapper.updateById(parentTask);
    }

    // ==================== 子任务处理 ====================

    private void handleChildTask(AdsSyncTaskDO task, AdsAccountCredentialDO credential) {
        if ("PENDING".equals(task.getStatus())) {
            submitReportRequest(task, credential);
        } else if ("RUNNING".equals(task.getStatus())) {
            pollAndProcessReport(task, credential);
        }
    }

    private void submitReportRequest(AdsSyncTaskDO task, AdsAccountCredentialDO credential) {
        Map<String, Object> ctx = task.getContext();
        String profileId = (String) ctx.get("profileId");
        String reportType = (String) ctx.get("reportType");
        String groupBy = (String) ctx.get("groupBy");
        String baseUrl = (String) ctx.get("baseUrl");
        String dateStr = task.getDateRangeStart().toString();
        String dateEnd = task.getDateRangeEnd().toString();

        try {
            Map<String, Object> configuration = new HashMap<>();
            configuration.put("adProduct", AmzSpReportConstants.AD_PRODUCT_SP);
            configuration.put("reportTypeId", reportType);
            
            // Use columns and groupBy from AmzSpReportConstants based on the type
            List<String> columns = AmzSpReportConstants.getColumnsForType(reportType, groupBy);
            configuration.put("columns", columns);
            if (groupBy != null && groupBy.contains(",")) {
                configuration.put("groupBy", Arrays.asList(groupBy.split(",")));
            } else {
                configuration.put("groupBy", Collections.singletonList(groupBy));
            }
            configuration.put("timeUnit", "DAILY");
            configuration.put("format", "GZIP_JSON");
//            configuration.put("date", "2026-02-10");

            // Save full configuration back to context for debugging as requested
            ctx.put("configuration", configuration);
            task.setContext(ctx);

            log.info("[submitReportRequest] 提交报表请求: taskId={}, reportType={}, groupBy={}, configuration={}", 
                task.getId(), reportType, groupBy, JSONUtil.toJsonStr(configuration));

            AmzReportRequest request = AmzReportRequest.builder()
                .name("Daily_SP_" + reportType + "_" + groupBy + "_" + dateStr)
                .startDate(dateStr)
                .endDate(dateEnd)
                .configuration(configuration)
                .build();

            AmzReportResponse response = reportApiService.createReport(credential, null, profileId, baseUrl, request);
            log.info("[submitReportRequest] {} 提交成功, reportId={}", task.getId(), response.getReportId());
            
            task.setPlatformJobId(response.getReportId());
            task.setStatus("RUNNING");
            adsSyncTaskMapper.updateById(task);
        } catch (Exception e) {
            log.error("[submitReportRequest] 子任务 {} 提交失败", task.getId(), e);
            handleTaskError(task, "Submit failed: " + e.getMessage());
        }
    }

    private void pollAndProcessReport(AdsSyncTaskDO task, AdsAccountCredentialDO credential) {
        Map<String, Object> ctx = task.getContext();
        String profileId = (String) ctx.get("profileId");
        String baseUrl = (String) ctx.get("baseUrl");
        String reportType = (String) ctx.get("reportType");

        try {
            AmzReportResponse statusResp = reportApiService.getReportStatus(
                credential, null, profileId, baseUrl, task.getPlatformJobId()
            );
            String amzStatus = statusResp.getStatus();

            if (AmzSpReportConstants.STATUS_COMPLETED.equals(amzStatus)) {
                byte[] content = reportApiService.downloadReport(statusResp.getUrl());
                Map<String, String> metadata = new HashMap<>();
                metadata.put("reportType", reportType);
                parseAndSave(content, task, metadata);

                task.setStatus("SUCCESS");
                task.setFinishedAt(LocalDateTime.now());
                adsSyncTaskMapper.updateById(task);
            } else if (AmzSpReportConstants.STATUS_FAILED.equals(amzStatus)) {
                task.setStatus("FAILED");
                task.setErrorMessage("Amazon report failed: " + statusResp.getErrorDetails());
                task.setFinishedAt(LocalDateTime.now());
                adsSyncTaskMapper.updateById(task);
            } else {
                log.info("[pollAndProcessReport] 子任务 {} 等待完成, amzStatus={}", task.getId(), amzStatus);
            }
        } catch (Exception e) {
            log.error("[pollAndProcessReport] 子任务 {} 轮询异常", task.getId(), e);
            handleTaskError(task, "Poll error: " + e.getMessage());
        }
    }

    private void handleTaskError(AdsSyncTaskDO task, String error) {
        task.setRetryCount(task.getRetryCount() + 1);
        if (task.getRetryCount() >= task.getMaxRetries()) {
            task.setStatus("FAILED");
            task.setFinishedAt(LocalDateTime.now());
        }
        task.setErrorMessage(error);
        adsSyncTaskMapper.updateById(task);
    }

    // ==================== 数据解析与入库逻辑 ====================

    private void parseAndSave(byte[] gzipContent, AdsSyncTaskDO task, Map<String, String> metadata) {
        String reportType = metadata.get("reportType");
        String groupBy = (String) task.getContext().get("groupBy");
        Long accountId = task.getAccountId();
        Long shopId = task.getShopId();

        byte[] unzipped = ZipUtil.unGzip(new ByteArrayInputStream(gzipContent));
        String jsonStr = new String(unzipped);

        JSONArray data = JSONUtil.parseArray(jsonStr);
        log.info("[parseAndSave] 开始同步 {} 条数据, reportType={}, groupBy={}, taskId={}", data.size(), reportType, groupBy, task.getId());

        // 用于 Campaign 维度的汇总 (仅当按 adGroup 分组时)
        Map<Long, AdsReportDailyDO> campaignAggregationMap = new HashMap<>();

        int successCount = 0;
        for (int i = 0; i < data.size(); i++) {
            try {
                JSONObject row = data.getJSONObject(i);
                processRow(accountId, shopId, reportType, groupBy, row, campaignAggregationMap);
                successCount++;
            } catch (Exception e) {
                log.error("[parseAndSave] 解析行数据失败: index={}", i, e);
            }
        }

        // 处理汇总后的 Campaign 数据
//        for (AdsReportDailyDO campaignReport : campaignAggregationMap.values()) {
//            saveReportDaily(campaignReport);
//            syncToSummary(accountId, "CAMPAIGN", campaignReport.getCampaignId(), campaignReport.getReportDate(),
//                BigDecimal.valueOf(((Number) campaignReport.getPayload().getOrDefault("cost", 0)).doubleValue()),
//                ((Number) campaignReport.getPayload().getOrDefault("impressions", 0L)).longValue(),
//                ((Number) campaignReport.getPayload().getOrDefault("clicks", 0L)).longValue(),
//                ((Number) campaignReport.getPayload().getOrDefault("purchases7d", 0)).intValue(),
//                BigDecimal.valueOf(((Number) campaignReport.getPayload().getOrDefault("sales7d", 0)).doubleValue()),
//                "CAMPAIGN", null, null);
//        }

        log.info("[parseAndSave] 报表处理完成: successCount={}, reportType={}", successCount, reportType);
    }

    private void processRow(Long accountId, Long shopId, String reportType, String groupBy, JSONObject row,
                            Map<Long, AdsReportDailyDO> campaignAggregationMap) {
        Long campaignId = null;
        Long adGroupId = null;

        // Resolve internal campaign/adGroup IDs
        String externalCampaignId = row.getStr("campaignId");
        if (externalCampaignId != null) {
            AdsCampaignDO campaign = adsCampaignMapper.selectByShopAndExternalId(shopId, externalCampaignId);
            if (campaign != null) campaignId = campaign.getId();
        }
        String externalAdGroupId = row.getStr("adGroupId");
        if (externalAdGroupId != null) {
            AdsAdGroupDO adGroup = adsAdGroupMapper.selectByShopAndExternalId(shopId, externalAdGroupId);
            if (adGroup != null) adGroupId = adGroup.getId();
        }

        if (campaignId == null && adGroupId == null) return;

        LocalDate reportDate = LocalDate.parse(row.getStr("date"));
        BigDecimal spend = row.getBigDecimal("cost", BigDecimal.ZERO);
        Long impressions = row.getLong("impressions", 0L);
        Long clicks = row.getLong("clicks", 0L);
        Integer conversions = row.getInt("purchases7d", 0);
        BigDecimal sales = row.getBigDecimal("sales7d", BigDecimal.ZERO);
        
        String targeting = row.getStr("targeting");
        String searchTerm = row.getStr("query"); // Amazon V3 uses 'query' for search term

        AdsReportDailyDO reportDO = AdsReportDailyDO.builder()
            .accountId(accountId)
            .groupColumn(groupBy)
            .campaignId(campaignId)
            .adGroupId(adGroupId)
            .reportDate(reportDate)
            .targeting(targeting)
            .searchTerm(searchTerm)
            .payload(row.toBean(LinkedHashMap.class))
            .build();

        saveReportDaily(reportDO);

        // 如果是按 adGroup 分组的 spCampaigns 报表，需要进行 Campaign 级别的聚合
//        if (AmzSpReportConstants.SP_CAMPAIGNS.equals(reportType) && "adGroup".equals(groupBy)) {
//            aggregateToCampaign(accountId, row, reportDate, spend, impressions, clicks, conversions, sales, campaignAggregationMap);
//        }
//
//        syncToSummary(accountId, groupColumn, campaignId != null ? campaignId : adGroupId, reportDate, spend, impressions, clicks, conversions, sales,
//            groupBy, row.getStr("placement"), searchTerm);
    }


    private void saveReportDaily(AdsReportDailyDO reportDO) {
        adsReportDailyMapper.insert(reportDO);
    }

    private void aggregateToCampaign(Long accountId, Long shopId, JSONObject row, LocalDate reportDate, 
                                     BigDecimal spend, Long impressions, Long clicks, 
                                     Integer conversions, BigDecimal sales,
                                     Map<Long, AdsReportDailyDO> campaignAggregationMap) {
        String externalCampaignId = row.getStr("campaignId");
        AdsCampaignDO campaign = adsCampaignMapper.selectByShopAndExternalId(shopId, externalCampaignId);
        if (campaign == null) return;

        Long campaignId = campaign.getId();
        AdsReportDailyDO campaignReport = campaignAggregationMap.get(campaignId);
        if (campaignReport == null) {
            campaignReport = AdsReportDailyDO.builder()
                .accountId(accountId)
                .groupColumn("CAMPAIGN")
                .campaignId(campaignId)
                .reportDate(reportDate)
                .payload(new HashMap<>())
                .build();
            // 初始化 payload 中的数值
            campaignReport.getPayload().put("cost", BigDecimal.ZERO);
            campaignReport.getPayload().put("impressions", 0L);
            campaignReport.getPayload().put("clicks", 0L);
            campaignReport.getPayload().put("purchases7d", 0);
            campaignReport.getPayload().put("sales7d", BigDecimal.ZERO);
            campaignAggregationMap.put(campaignId, campaignReport);
        }

        // 累加
        Map<String, Object> payload = campaignReport.getPayload();
        payload.put("impressions", ((Number) payload.getOrDefault("impressions", 0L)).longValue() + impressions);
        payload.put("clicks", ((Number) payload.getOrDefault("clicks", 0L)).longValue() + clicks);
        payload.put("cost", ((BigDecimal) payload.getOrDefault("cost", BigDecimal.ZERO)).add(spend));
        payload.put("purchases7d", ((Number) payload.getOrDefault("purchases7d", 0)).intValue() + conversions);
        payload.put("sales7d", ((BigDecimal) payload.getOrDefault("sales7d", BigDecimal.ZERO)).add(sales));
    }

    private void syncToSummary(Long accountId, String entityType, Long entityId,
                                LocalDate reportDate, BigDecimal spend, Long impressions,
                                Long clicks, Integer conversions, BigDecimal sales,
                                String groupBy, String placement, String searchTerm) {
        String periodValue = reportDate.toString(); 

        AdsReportSummaryDO summary = adsReportSummaryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportSummaryDO>()
                .eq(AdsReportSummaryDO::getAccountId, accountId)
                .eq(AdsReportSummaryDO::getEntityType, entityType)
                .eq(AdsReportSummaryDO::getEntityId, entityId)
                .eq(AdsReportSummaryDO::getPeriodType, "DAY")
                .eq(AdsReportSummaryDO::getPeriodValue, periodValue)
                .eq(AdsReportSummaryDO::getGroupBy, groupBy)
                .eq(AdsReportSummaryDO::getPlacement, placement)
                .eq(AdsReportSummaryDO::getSearchTerm, searchTerm)
        );

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("spend", spend);
        metrics.put("impressions", impressions);
        metrics.put("clicks", clicks);
        metrics.put("orders", conversions);
        metrics.put("sales", sales);

        if (summary == null) {
            summary = AdsReportSummaryDO.builder()
                .accountId(accountId)
                .entityType(entityType)
                .entityId(entityId)
                .periodType("DAY")
                .periodValue(periodValue)
                .groupBy(groupBy)
                .placement(placement)
                .searchTerm(searchTerm)
                .metrics(metrics)
                .build();
            adsReportSummaryMapper.insert(summary);
        } else {
            summary.setMetrics(metrics);
            adsReportSummaryMapper.updateById(summary);
        }
    }

    private String getExternalId(String reportType, JSONObject row) {
        if (AmzSpReportConstants.SP_CAMPAIGNS.equals(reportType)) {
            // For campaigns grouped by adGroup, we use adGroupId as the primary external ID for the record
            String adGroupId = row.getStr("adGroupId");
            return adGroupId != null ? adGroupId : row.getStr("campaignId");
        }
        if (AmzSpReportConstants.SP_TARGETING.equals(reportType)) return row.getStr("targetId");
        if (AmzSpReportConstants.SP_SEARCH_TERM.equals(reportType)) return row.getStr("targetId");
        return null;
    }

    private String getInternalEntityType(String reportType, String groupBy) {
        if (AmzSpReportConstants.SP_SEARCH_TERM.equals(reportType)) return "SEARCH_TERM";
        if (AmzSpReportConstants.SP_TARGETING.equals(reportType)) return "KEYWORD";
        if (AmzSpReportConstants.SP_CAMPAIGNS.equals(reportType)) {
            if ("adGroup".equals(groupBy)) return "ADGROUP";
            return "CAMPAIGN";
        }
        return "UNKNOWN";
    }

    private Long findInternalEntityId(Long shopId, String reportType, String externalId, JSONObject row) {
        if (AmzSpReportConstants.SP_CAMPAIGNS.equals(reportType)) {
            // Check if it's an adGroup record or campaign record
            String adGroupIdStr = row.getStr("adGroupId");
            if (adGroupIdStr != null) {
                AdsAdGroupDO adGroup = adsAdGroupMapper.selectByShopAndExternalId(shopId, adGroupIdStr);
                return adGroup != null ? adGroup.getId() : null;
            }
            AdsCampaignDO campaign = adsCampaignMapper.selectByShopAndExternalId(shopId, externalId);
            return campaign != null ? campaign.getId() : null;
        }
        if (AmzSpReportConstants.SP_TARGETING.equals(reportType) || AmzSpReportConstants.SP_SEARCH_TERM.equals(reportType)) {
            AdsKeywordDO keyword = adsKeywordMapper.selectByShopAndExternalId(shopId, externalId);
            return keyword != null ? keyword.getId() : null;
        }
        return null;
    }
}
