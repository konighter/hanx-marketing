package com.hzltd.module.amz.spapi.api;

import com.amazon.SellingPartnerAPIAA.LWAAccessTokenCacheImpl;
import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.reports.v2021_06_30.ReportsApi;
import software.amazon.spapi.models.reports.v2021_06_30.CreateReportResponse;
import software.amazon.spapi.models.reports.v2021_06_30.CreateReportSpecification;
import software.amazon.spapi.models.reports.v2021_06_30.Report;
import software.amazon.spapi.models.reports.v2021_06_30.ReportDocument;

import java.util.List;

@Slf4j
@Service
public class AmazonReportService extends AbsAmzPlatformApiService {
    @Resource
    private SystemShopService systemShopService;

    // 51769020430 是测试用的ReportId
    // B0FGYCJT9S 测试用的ASIN
    public String createReport(ApiRequest<CreateReportSpecification> apiRequest) {

        ReportsApi reportsApi = getReportsApi(apiRequest);
        List<String> marketPlaceIds = systemShopService.getShopRegion(apiRequest.getShopId());
        try {
            CreateReportResponse createReportResponse = reportsApi.createReport(apiRequest.getRequest().marketplaceIds(marketPlaceIds));
            log.info("createReportResponse: {}", createReportResponse);
            return createReportResponse.getReportId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
/**
 *  1、51771020431 doing GET_BRAND_ANALYTICS_SEARCH_CATALOG_PERFORMANCE_REPORT
 *
 *
  */

    public Report checkReportStatus(ApiRequest<String> apiRequest) {
        ReportsApi reportsApi = getReportsApi(apiRequest);

        try {
            Report report = reportsApi.getReport(apiRequest.getRequest());
            log.info("report: {}", report);
            return report;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (LWAException e) {
            throw new RuntimeException(e);
        }

    }

    public ReportDocument getReportDocument(ApiRequest<String> apiRequest) {
        String reportId = apiRequest.getRequest();
        ReportsApi reportsApi = getReportsApi(apiRequest);

        try {
            ReportDocument reportDocument = reportsApi.getReportDocument(reportId);
            log.info("reportDocument: {}", reportDocument);
            return reportDocument;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (LWAException e) {
            throw new RuntimeException(e);
        }
    }

    private final LWAAccessTokenCacheImpl lwaAccessTokenCache = new LWAAccessTokenCacheImpl();

    private ReportsApi getReportsApi(ApiRequest<?> apiRequest) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(apiRequest);
        List<String> marketPlaceIds = systemShopService.getShopRegion(apiRequest.getShopId());
        return new ReportsApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(lwaAccessTokenCache)
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }


}
