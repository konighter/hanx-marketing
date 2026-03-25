package com.hzltd.module.amz.api.adv.v1;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.api.adv.AbstractAmazonAdsAdapter;
import com.hzltd.module.amz.api.adv.model.AdvRequest;
import com.hzltd.module.amz.api.adv.model.AdvResponse;
import com.hzltd.module.amz.api.adv.v1.model.AmzReportRequest;
import com.hzltd.module.amz.api.adv.v1.model.AmzReportResponse;
import com.hzltd.module.amz.api.adv.v1.model.AmzStreamSubscriptionRequest;
import com.hzltd.module.amz.api.adv.v1.model.AmzStreamSubscriptionResponse;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Amazon Ads Reporting API V3.0 Service
 */
@Slf4j
@Service
public class AmazonAdsReportApiService extends AbstractAmazonAdsAdapter {

    @Resource
    private AdsApiClient apiClient;

    private static final String REPORTING_BASE_PATH = "/reporting/reports";
    private static final String STREAMS_BASE_PATH = "/streams/subscriptions";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Create a report
     */
    public AmzReportResponse createReport(AdsAccountCredentialDO credential, String accountId,
                                          String profileId, String baseUrl, AmzReportRequest request) {
        String url = baseUrl + REPORTING_BASE_PATH;
        log.info("[createReport] accountId={}, profileId={}", accountId, profileId);

        String resp = apiClient.post(credential, accountId, profileId, url, request);
        return JsonUtils.parseObject(resp, AmzReportResponse.class);
    }

    /**
     * Get report status
     */
    public AmzReportResponse getReportStatus(AdsAccountCredentialDO credential, String accountId,
                                            String profileId, String baseUrl, String reportId) {
        String url = baseUrl + REPORTING_BASE_PATH + "/" + reportId;
        log.info("[getReportStatus] accountId={}, reportId={}", accountId, reportId);

        String resp = apiClient.get(credential, accountId, profileId, url);
        return JsonUtils.parseObject(resp, AmzReportResponse.class);
    }

    public AmzReportResponse getReportStatus(AdvRequest<String> request) {
        String url = REPORTING_BASE_PATH + "/" + request.getData();
        log.info("[getReportStatus] accountId={}, reportId={}", request.getAccountId(), request.getData());

        AdvResponse<String> resp = apiClient.get(request, url);
        return JsonUtils.parseObject(resp.getData(), AmzReportResponse.class);
    }

    /**
     * Download report content (GZIP JSON)
     * Note: The download URL is a pre-signed S3 URL, so we use a standard HttpClient without Amazon Ads headers.
     */
    public byte[] downloadReport(String downloadUrl) {
        log.info("[downloadReport] url={}", downloadUrl);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(downloadUrl))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                log.error("[downloadReport] Failed to download report, status={}, body={}", 
                        response.statusCode(), new String(response.body()));
                throw new RuntimeException("Failed to download report from Amazon: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("[downloadReport] Exception during download", e);
            throw new RuntimeException("Exception during report download", e);
        }
    }

    /**
     * 创建 Stream Subscription (订阅广告实时数据)
     *
     * @param credential 账户凭证
     * @param accountId  广告账户 ID
     * @param profileId  亚马逊 Profile ID
     * @param baseUrl    区域 API 基础 URL
     * @param request    订阅请求 (dataSetId, destinationDetails 等)
     * @return 订阅响应 (含 subscriptionId, status)
     */
    public AmzStreamSubscriptionResponse createStreamSubscription(AdsAccountCredentialDO credential, String accountId,
                                                                  String profileId, String baseUrl,
                                                                  AmzStreamSubscriptionRequest request) {
        String url = baseUrl + STREAMS_BASE_PATH;
        log.info("[createStreamSubscription] accountId={}, profileId={}, dataSetId={}", accountId, profileId, request.getDataSetId());

        String resp = apiClient.post(credential, accountId, profileId, url, request);
        return JsonUtils.parseObject(resp, AmzStreamSubscriptionResponse.class);
    }

    /**
     * 获取 Stream Subscription 状态
     *
     * @param credential     账户凭证
     * @param accountId      广告账户 ID
     * @param profileId      亚马逊 Profile ID
     * @param baseUrl        区域 API 基础 URL
     * @param subscriptionId 订阅 ID
     * @return 订阅详情 (含 status, destinationDetails, dataSetId)
     */
    public AmzStreamSubscriptionResponse getStreamSubscription(AdsAccountCredentialDO credential, String accountId,
                                                                String profileId, String baseUrl, String subscriptionId) {
        String url = baseUrl + STREAMS_BASE_PATH + "/" + subscriptionId;
        log.info("[getStreamSubscription] accountId={}, subscriptionId={}", accountId, subscriptionId);

        String resp = apiClient.get(credential, accountId, profileId, url);
        return JsonUtils.parseObject(resp, AmzStreamSubscriptionResponse.class);
    }

    public List<AmzStreamSubscriptionResponse> listStreamSubscription(AdsAccountCredentialDO credential, String accountId,
                                                                      String profileId, String baseUrl) {
        String url = baseUrl + STREAMS_BASE_PATH ;
        log.info("[listStreamSubscription] accountId={}", accountId);

        String resp = apiClient.get(credential, accountId, profileId, url);

        JSONObject respObj = JSONUtil.parseObj(resp);
        return respObj.getBeanList("subscriptions", AmzStreamSubscriptionResponse.class);

//        return JsonUtils.parseArray(resp, AmzStreamSubscriptionResponse.class);
    }

}

