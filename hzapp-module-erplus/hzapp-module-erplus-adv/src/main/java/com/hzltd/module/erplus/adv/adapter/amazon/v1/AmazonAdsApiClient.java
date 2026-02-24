package com.hzltd.module.erplus.adv.adapter.amazon.v1;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.infra.dal.dataobject.config.ConfigDO;
import com.hzltd.module.infra.service.config.ConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Amazon Ads V1 API 底层 HTTP 客户端
 *
 * 职责：
 * - 封装 HTTP 请求构建（GET / POST / PUT / DELETE）
 * - 自动注入 Amazon 认证头：
 *     Amazon-Ads-AccountId   — 广告账户 ID
 *     Amazon-Ads-ClientId    — LWA Client ID
 *     Amazon-Advertising-API-Scope — Profile ID（站点维度）
 * - 统一响应处理与错误日志
 *
 * 不包含任何业务逻辑，仅负责 HTTP 通信。
 */
@Slf4j
@Component
public class AmazonAdsApiClient {

    private static final String AMZ_CLIENT_ID = "amz_ads_client_id";

    @Resource
    private ConfigService configService;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ==================== Public API ====================

    /**
     * 发送 GET 请求
     */
    public String get(AdsAccountCredentialDO credential, String accountId, String profileId, String url) {
        HttpRequest request = buildRequest(credential, accountId, profileId, url)
                .GET()
                .build();
        return executeRequest(request, url);
    }

    /**
     * 发送 POST 请求（JSON body）
     */
    public String post(AdsAccountCredentialDO credential, String accountId, String profileId, String url, Object body) {
        String jsonBody = body instanceof String ? (String) body : JsonUtils.toJsonString(body);
        HttpRequest request = buildRequest(credential, accountId, profileId, url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return executeRequest(request, url);
    }

    /**
     * 发送 PUT 请求（JSON body）
     */
    public String put(AdsAccountCredentialDO credential, String accountId, String profileId, String url, Object body) {
        String jsonBody = body instanceof String ? (String) body : JsonUtils.toJsonString(body);
        HttpRequest request = buildRequest(credential, accountId, profileId, url)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return executeRequest(request, url);
    }

    /**
     * 发送 DELETE 请求
     */
    public String delete(AdsAccountCredentialDO credential, String accountId, String profileId, String url) {
        HttpRequest request = buildRequest(credential, accountId, profileId, url)
                .DELETE()
                .build();
        return executeRequest(request, url);
    }

    // ==================== Internal ====================

    private HttpRequest.Builder buildRequest(AdsAccountCredentialDO credential, String accountId,
                                              String profileId, String url) {
        String clientId = Optional.ofNullable(configService.getConfigByKey(AMZ_CLIENT_ID))
                .map(ConfigDO::getValue)
                .orElseThrow(() -> new AmazonAdsApiException(0, "Missing config: " + AMZ_CLIENT_ID, url));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + credential.getAccessToken())
                .header("Amazon-Ads-ClientId", clientId);

        // 广告账户 ID
        if (StringUtils.isNotEmpty(accountId)) {
            builder.header("Amazon-Ads-AccountId", accountId);
        }
        // Profile ID（站点维度）
        if (StringUtils.isNotEmpty(profileId)) {
            builder.header("Amazon-Advertising-API-Scope", profileId);
        }

        return builder;
    }

    private String executeRequest(HttpRequest request, String url) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                log.debug("[AmazonAdsApiClient] {} {} → {}", request.method(), url, statusCode);
                return response.body();
            } else {
                log.error("[AmazonAdsApiClient] {} {} → {} body={}", request.method(), url, statusCode, response.body());
                throw new AmazonAdsApiException(statusCode, response.body(), url);
            }
        } catch (AmazonAdsApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[AmazonAdsApiClient] {} {} 网络异常", request.method(), url, e);
            throw new AmazonAdsApiException(0, e.getMessage(), url);
        }
    }
}
