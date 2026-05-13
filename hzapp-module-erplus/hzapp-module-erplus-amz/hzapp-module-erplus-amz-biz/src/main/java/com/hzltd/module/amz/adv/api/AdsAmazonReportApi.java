package com.hzltd.module.amz.adv.api;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;

import com.hzltd.module.amz.adv.api.enums.AmzSpReportConstants;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.report.api.AsynchronousReportsApi;
import com.hzltd.module.amz.adv.client.report.model.*;
import com.hzltd.module.amz.adv.service.AdsAmazonProfileService;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.api.AdsServiceRegister;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportBatchDO;
import com.hzltd.module.erplus.adv.enums.AdsReportStatus;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.adv.service.AdsReportApi;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.system.annotation.CrossplatformApiLog;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

/**
 * Amazon 广告报表 API 实现
 * 
 * 使用 AsynchronousReportsApi (V3) 实现
 */
@Slf4j
@Service
@AdsServiceRegister(platform = AdsPlatformEnum.AMAZON, serviceClass = AdsReportApi.class)
public class AdsAmazonReportApi extends AbstractAmazonAdsService implements AdsReportApi {


    @Resource
    private AdsAmazonProfileService adsAmazonProfileService;

    @Override
    @CrossplatformApiLog
    public AdsResponse<List<AdsReportRequest>> getReportRequests(AdsRequest<AdsReportGetRequest> request) {
        List<AdsAmazonProfileDO> profiles = adsAmazonProfileService.getProfileListByShopId(request.getShopId());
        if (profiles.isEmpty()) {
            return AdsResponse.success(Collections.emptyList());
        }

        List<AdsReportRequest> reportRequests = new ArrayList<>();
        AdsReportGetRequest query = request.getRequest();

        for (AdsAmazonProfileDO profile : profiles) {
            // Sponsored Products (SP)
            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SP_CAMPAIGNS, "campaign", "CAMPAIGN"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SP_CAMPAIGNS, "campaignPlacement", "PLACEMENT"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SP_AD_GROUPS, "adGroup", "AD_GROUP"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SP_ADS, "ad", "AD"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SP_TARGETING, "targeting", "TARGETING"));
            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SP_SEARCH_TERM, "searchTerm", "SEARCH_TERM"));

            // 暂时只支持SP广告

//            // Sponsored Brands (SB)
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SB_CAMPAIGNS, "campaign", "CAMPAIGN"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SB_AD_GROUPS, "adGroup", "AD_GROUP"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SB_ADS, "ad", "AD"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SB_TARGETING, "targeting", "TARGETING"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SB_SEARCH_TERM, "searchTerm", "SEARCH_TERM"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SB_PLACEMENT, "placement", "PLACEMENT"));
//
//            // Sponsored Display (SD)
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SD_CAMPAIGNS, "campaign", "CAMPAIGN"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SD_AD_GROUPS, "adGroup", "AD_GROUP"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SD_ADS, "ad", "AD"));
//            reportRequests.add(buildAmazonRequest(request.getShopId(), profile, query.getStartDate(), query.getEndDate(), AmzSpReportConstants.SD_TARGETING, "targeting", "TARGETING"));
        }

        return AdsResponse.success(reportRequests);
    }

    private AdsReportRequest buildAmazonRequest(Long shopId, AdsAmazonProfileDO profile, LocalDate start, LocalDate end, String reportType, String groupBy, String recordType) {
        Map<String, Object> context = new HashMap<>();
        context.put("profileId", profile.getProfileId());
        context.put("reportType", reportType);
        context.put("groupBy", groupBy);
        context.put("recordType", recordType);
        context.put("region", profile.getRegion());

        return AdsReportRequest.builder()
                .shopId(shopId.intValue())
                .platform("AMAZON")
                .startDate(start)
                .endDate(end)
                .context(context)
                .build();
    }

    @Override
    @CrossplatformApiLog
    public AdsResponse<String> submitAsyncReport(AdsRequest<AdsReportRequest> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            AsynchronousReportsApi reportsApi = new AsynchronousReportsApi(getApiClient(authModel));

            AdsReportRequest reportRequest = request.getRequest();
            Map<String, Object> ctx = reportRequest.getContext();
            
            String reportType = (String) ctx.get("reportType");
            String groupBy = (String) ctx.get("groupBy");

            CreateAsyncReportRequest apiRequest = new CreateAsyncReportRequest()
                    .startDate(reportRequest.getStartDate().toString())
                    .endDate(reportRequest.getEndDate().toString())
                    .name(reportType+"_"+reportRequest.getEndDate().toString()+"_"+request.getShopId())
                    ._configuration(new AsyncReportConfiguration()
                            .adProduct(mapAdProduct(reportType))
                            .reportTypeId(reportType)
                            .format(AsyncReportConfiguration.FormatEnum.GZIP_JSON)
                            .timeUnit(AsyncReportConfiguration.TimeUnitEnum.DAILY)
                            .columns(AmzSpReportConstants.getColumnsForType(reportType, groupBy))
                            .groupBy(Arrays.asList(groupBy.split(",")))
                    );

            AsyncReport response = reportsApi.createAsyncReport(authModel.getAppKey(), authModel.getProfileId(), "amzn1.ads-account.g.46urv0w7o9cejxduu598uh7vs", apiRequest);
            return AdsResponse.success(response.getReportId());
        } catch (ApiException e) {
            log.error("Failed to submit Amazon report", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    private AsyncReportAdProduct mapAdProduct(String reportType) {
        if (reportType.startsWith("sp")) return AsyncReportAdProduct.SPONSORED_PRODUCTS;
        if (reportType.startsWith("sb")) return AsyncReportAdProduct.SPONSORED_BRANDS;
        if (reportType.startsWith("sd")) return AsyncReportAdProduct.SPONSORED_DISPLAY;
        if (reportType.startsWith("st")) return AsyncReportAdProduct.SPONSORED_TELEVISION;
        return AsyncReportAdProduct.SPONSORED_PRODUCTS;
    }

    @Override
    @CrossplatformApiLog
    public AdsResponse<AdsReportStatus> getReportStatus(AdsRequest<AdsReportStatusRequest> request) {
        try {
            AdsReportStatusRequest statusRequest = request.getRequest();
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            AsynchronousReportsApi reportsApi = new AsynchronousReportsApi(getApiClient(authModel));

            AsyncReport response = reportsApi.getAsyncReport(statusRequest.getPlatformJobId(), authModel.getAppKey(), authModel.getProfileId(), null);
            
            AdsReportStatus status = mapStatus(response.getStatus());
            return AdsResponse.success(status);
        } catch (ApiException e) {
            log.error("Failed to get Amazon report status", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    private AdsReportStatus mapStatus(AsyncReport.StatusEnum amzStatus) {
        if (AsyncReport.StatusEnum.COMPLETED.equals(amzStatus)) return AdsReportStatus.COMPLETED;
        if (AsyncReport.StatusEnum.FAILED.equals(amzStatus)) return AdsReportStatus.FAILED;
        if (AsyncReport.StatusEnum.PROCESSING.equals(amzStatus)) return AdsReportStatus.PROCESSING;
        if (AsyncReport.StatusEnum.PENDING.equals(amzStatus)) return AdsReportStatus.PROCESSING;
        return AdsReportStatus.NOT_FOUND;
    }

    @Override
    @CrossplatformApiLog
    public AdsResponse<Void> downloadAndProcess(AdsRequest<AdsReportProcessRequest> request, Consumer<List<?>> saver) {
        try {
            AdsReportProcessRequest processRequest = request.getRequest();
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            AsynchronousReportsApi reportsApi = new AsynchronousReportsApi(getApiClient(authModel));

            // 1. 获取下载 URL
            AsyncReport report = reportsApi.getAsyncReport(processRequest.getPlatformJobId(), authModel.getAppKey(), authModel.getProfileId(), null);
            if (!AsyncReport.StatusEnum.COMPLETED.equals(report.getStatus())) {
                return AdsResponse.error("Report not ready");
            }

            // 2. 下载数据
            byte[] compressedData = downloadData(report.getUrl());
            
            // 3. 解压并解析 (GZIP JSON)
            byte[] jsonBytes = ZipUtil.unGzip(new ByteArrayInputStream(compressedData));
            String jsonContent = new String(jsonBytes);
            JSONArray dataArray = JSONUtil.parseArray(jsonContent);

            // 4. 转换并保存
            List<AdsReportBatchDO> batch = new ArrayList<>();
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject row = dataArray.getJSONObject(i);
                batch.add(mapToBatchDO(request.getShopId(), processRequest.getReportRequest(), row));
                
                // 分批次交给 saver 以防止内存溢出
                if (batch.size() >= 500) {
                    saver.accept(new ArrayList<>(batch));
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                saver.accept(batch);
            }

            return AdsResponse.success(null);
        } catch (Exception e) {
            log.error("Failed to download and process Amazon report", e);
            return AdsResponse.error(e.getMessage());
        }
    }

    private byte[] downloadData(String url) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Download failed with status: " + response.statusCode());
        }
        return response.body();
    }

    private AdsReportBatchDO mapToBatchDO(Long shopId, AdsReportRequest reportRequest, JSONObject row) {
        Map<String, Object> ctx = reportRequest.getContext();
        String recordType = (String) ctx.get("recordType");

        // 解析公共维度
        String dateStr = row.getStr("date");
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : reportRequest.getStartDate();
        String campaignId = row.getStr("campaignId");
        String campaignName = row.getStr("campaignName");
        String adGroupId = row.getStr("adGroupId");
        String adGroupName = row.getStr("adGroupName");
        String adId = row.getStr("adId");
        String adName = row.getStr("adName");
        String keywordId = row.getStr("keywordId");
        String keywordText = row.getStr("keyword");
        if (keywordText == null) keywordText = row.getStr("targeting"); // SP targeting uses 'targeting'
        
        String placement = row.getStr("placementClassification"); // Amazon V3 uses placementClassification

        // 解析指标
        return AdsReportBatchDO.builder()
                .reportDate(date)
                .shopId(shopId)
                .platform("AMAZON")
                .campaignId(campaignId)
                .campaignName(campaignName)
                .adGroupId(adGroupId)
                .adGroupName(adGroupName)
                .adId(adId)
                .adName(adName)
                .keywordId(keywordId)
                .keywordText(keywordText)
                .placement(placement)
                .recordType(recordType)
                .searchTerm(row.getStr("searchTerm"))
                .productAsin(row.getStr("asin"))
                .impressions(row.getLong("impressions", 0L))
                .clicks(row.getLong("clicks", 0L))
                .cost(row.getBigDecimal("cost", BigDecimal.ZERO))
                .sales(row.getBigDecimal("sales7d", BigDecimal.ZERO)) // 默认取 7d
                .orders(row.getLong("purchases7d", 0L))
                // Amazon 扩展归因
                .amzAttributedSales1d(row.getBigDecimal("sales1d", BigDecimal.ZERO))
                .amzAttributedSales7d(row.getBigDecimal("sales7d", BigDecimal.ZERO))
                .amzAttributedSales14d(row.getBigDecimal("sales14d", BigDecimal.ZERO))
                .amzAttributedSales30d(row.getBigDecimal("sales30d", BigDecimal.ZERO))
                .amzAttributedUnitsOrdered1d(row.getLong("purchases1d", 0L))
                .amzAttributedUnitsOrdered7d(row.getLong("purchases7d", 0L))
                .amzAttributedUnitsOrdered14d(row.getLong("purchases14d", 0L))
                .amzAttributedUnitsOrdered30d(row.getLong("purchases30d", 0L))
                .amzAttributedSales1dSameSku(row.getBigDecimal("attributedSalesSameSku1d", BigDecimal.ZERO))
                .amzAttributedSales7dSameSku(row.getBigDecimal("attributedSalesSameSku7d", BigDecimal.ZERO))
                .amzAttributedSales14dSameSku(row.getBigDecimal("attributedSalesSameSku14d", BigDecimal.ZERO))
                .amzAttributedSales30dSameSku(row.getBigDecimal("attributedSalesSameSku30d", BigDecimal.ZERO))
                .amzAttributedUnitsOrdered1dSameSku(row.getLong("purchasesSameSku1d", 0L))
                .amzAttributedUnitsOrdered7dSameSku(row.getLong("purchasesSameSku7d", 0L))
                .amzAttributedUnitsOrdered14dSameSku(row.getLong("purchasesSameSku14d", 0L))
                .amzAttributedUnitsOrdered30dSameSku(row.getLong("purchasesSameSku30d", 0L))
                .build();
    }
}
