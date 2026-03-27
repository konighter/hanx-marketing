package com.hzltd.module.amz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.spapi.model.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * 亚马逊广告运营 API 服务
 * 提供与亚马逊广告API的集成能力，用于实际的广告操作
 */
@Slf4j
@Service
public class AmzAdvOperationApiAdsService extends AbstractAmazonAdsService {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * 创建广告活动
     *
     * @param apiRequest 请求对象
     * @param profileId  广告配置文件 ID
     * @param campaignData 广告活动数据
     * @return campaignId
     */
    public String createCampaign(ApiRequest<?> apiRequest, String profileId, String campaignData) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/v2/sp/campaigns";

        RequestBody body = RequestBody.create(JSON, campaignData);
        Request request = newAuthenticatedRequestBuilder(apiRequest, url, profileId).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to create Amazon Adv campaign: {}", responseBody);
                throw new RuntimeException("Failed to create Amazon Adv campaign: " + responseBody);
            }

            JsonNode responseJson = JsonUtils.parseTree(responseBody);
            // 通常响应会包含新创建的广告活动ID
            JsonNode campaignNode = responseJson.isArray() ? responseJson.get(0) : responseJson;
            return campaignNode.has("campaignId") ? campaignNode.get("campaignId").asText() : null;
        } catch (IOException e) {
            log.error("Error creating Amazon Adv campaign", e);
            throw new RuntimeException("Error creating Amazon Adv campaign", e);
        }
    }

    /**
     * 更新广告活动
     *
     * @param apiRequest 请求对象
     * @param profileId  广告配置文件 ID
     * @param campaignData 广告活动更新数据
     */
    public void updateCampaign(ApiRequest<?> apiRequest, String profileId, String campaignData) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/v2/sp/campaigns";

        RequestBody body = RequestBody.create(JSON, campaignData);
        Request request = newAuthenticatedRequestBuilder(apiRequest, url, profileId).put(body).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to update Amazon Adv campaign: {}", responseBody);
                throw new RuntimeException("Failed to update Amazon Adv campaign: " + responseBody);
            }
        } catch (IOException e) {
            log.error("Error updating Amazon Adv campaign", e);
            throw new RuntimeException("Error updating Amazon Adv campaign", e);
        }
    }

    /**
     * 获取广告活动列表
     *
     * @param apiRequest 请求对象
     * @param profileId  广告配置文件 ID
     * @param filters    过滤条件
     * @return 广告活动列表
     */
    public JsonNode listCampaigns(ApiRequest<?> apiRequest, String profileId, String filters) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/v2/sp/campaigns";
        if (filters != null && !filters.isEmpty()) {
            url += "?" + filters;
        }

        Request request = newAuthenticatedRequestBuilder(apiRequest, url, profileId).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to list Amazon Adv campaigns: {}", responseBody);
                throw new RuntimeException("Failed to list Amazon Adv campaigns: " + responseBody);
            }

            return JsonUtils.parseTree(responseBody);
        } catch (IOException e) {
            log.error("Error listing Amazon Adv campaigns", e);
            throw new RuntimeException("Error listing Amazon Adv campaigns", e);
        }
    }

    /**
     * 创建广告组
     *
     * @param apiRequest 请求对象
     * @param profileId  广告配置文件 ID
     * @param adGroupData 广告组数据
     * @return adGroupId
     */
    public String createAdGroup(ApiRequest<?> apiRequest, String profileId, String adGroupData) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/v2/sp/adGroups";

        RequestBody body = RequestBody.create(JSON, adGroupData);
        Request request = newAuthenticatedRequestBuilder(apiRequest, url, profileId).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to create Amazon Adv ad group: {}", responseBody);
                throw new RuntimeException("Failed to create Amazon Adv ad group: " + responseBody);
            }

            JsonNode responseJson = JsonUtils.parseTree(responseBody);
            JsonNode adGroupNode = responseJson.isArray() ? responseJson.get(0) : responseJson;
            return adGroupNode.has("adGroupId") ? adGroupNode.get("adGroupId").asText() : null;
        } catch (IOException e) {
            log.error("Error creating Amazon Adv ad group", e);
            throw new RuntimeException("Error creating Amazon Adv ad group", e);
        }
    }

    /**
     * 创建关键词
     *
     * @param apiRequest 请求对象
     * @param profileId  广告配置文件 ID
     * @param keywordData 关键词数据
     * @return keywordId
     */
    public String createKeyword(ApiRequest<?> apiRequest, String profileId, String keywordData) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/v2/sp/keywords";

        RequestBody body = RequestBody.create(JSON, keywordData);
        Request request = newAuthenticatedRequestBuilder(apiRequest, url, profileId).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to create Amazon Adv keyword: {}", responseBody);
                throw new RuntimeException("Failed to create Amazon Adv keyword: " + responseBody);
            }

            JsonNode responseJson = JsonUtils.parseTree(responseBody);
            JsonNode keywordNode = responseJson.isArray() ? responseJson.get(0) : responseJson;
            return keywordNode.has("keywordId") ? keywordNode.get("keywordId").asText() : null;
        } catch (IOException e) {
            log.error("Error creating Amazon Adv keyword", e);
            throw new RuntimeException("Error creating Amazon Adv keyword", e);
        }
    }

    /**
     * 获取广告表现报告
     *
     * @param apiRequest 请求对象
     * @param profileId  广告配置文件 ID
     * @param reportDate 报告日期
     * @param campaignIds 广告活动ID列表
     * @return 报告数据
     */
    public JsonNode getCampaignPerformance(ApiRequest<?> apiRequest, String profileId, String reportDate, String campaignIds) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/v2/sp/campaigns/extended";

        // 构建查询参数
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (campaignIds != null) {
            httpBuilder.addQueryParameter("campaignIdFilter", campaignIds);
        }

        Request request = newAuthenticatedRequestBuilder(apiRequest, httpBuilder.build().toString(), profileId).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to get campaign performance: {}", responseBody);
                throw new RuntimeException("Failed to get campaign performance: " + responseBody);
            }

            return JsonUtils.parseTree(responseBody);
        } catch (IOException e) {
            log.error("Error getting campaign performance", e);
            throw new RuntimeException("Error getting campaign performance", e);
        }
    }
}