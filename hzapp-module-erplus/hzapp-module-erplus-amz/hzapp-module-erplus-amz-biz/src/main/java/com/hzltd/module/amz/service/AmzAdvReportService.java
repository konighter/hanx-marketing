package com.hzltd.module.amz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.spapi.model.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class AmzAdvReportService extends AbsAmzAdvService {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * 1. 发起报告请求
     *
     * @param apiRequest 请求对象
     * @param profileId  广告文件 ID
     * @param reportType 报告类型 (例如 spCampaigns)
     * @param bodyJson   报告请求参数
     * @return reportId
     */
    public String requestReport(ApiRequest<?> apiRequest, String profileId, String reportType, String bodyJson) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/reporting/reports";

        // 确保 bodyJson 中包含 reportTypeId
        ObjectNode bodyNode = (ObjectNode) JsonUtils.parseTree(bodyJson);
        bodyNode.put("reportTypeId", reportType);

        RequestBody body = RequestBody.create(JSON, bodyNode.toString());
        Request request = newAuthenticatedRequestBuilder(apiRequest, url, profileId).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to request Amazon Adv report: {}", responseBody);
                throw new RuntimeException("Failed to request Amazon Adv report: " + responseBody);
            }

            JsonNode responseJson = JsonUtils.parseTree(responseBody);
            return responseJson.get("reportId").asText();
        } catch (IOException e) {
            log.error("Error requesting Amazon Adv report", e);
            throw new RuntimeException("Error requesting Amazon Adv report", e);
        }
    }

    /**
     * 2. 查询报告状态
     *
     * @param apiRequest 请求对象
     * @param profileId  广告文件 ID
     * @param reportId   报告 ID
     * @return 状态响应 JsonNode
     */
    public JsonNode getReportStatus(ApiRequest<?> apiRequest, String profileId, String reportId) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/reporting/reports/" + reportId;
        Request request = newAuthenticatedRequestBuilder(apiRequest, url, profileId).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to get Amazon Adv report status: {}", responseBody);
                throw new RuntimeException("Failed to get Amazon Adv report status: " + responseBody);
            }

            return JsonUtils.parseTree(responseBody);
        } catch (IOException e) {
            log.error("Error getting Amazon Adv report status", e);
            throw new RuntimeException("Error getting Amazon Adv report status", e);
        }
    }

    /**
     * 3. 下载报告
     *
     * @param downloadUrl 报告下载 URL (从状态响应获取)
     * @return 报告内容 (通常是 GZIP 压缩的 JSON)
     */
    public byte[] downloadReport(String downloadUrl) {
        Request request = new Request.Builder().url(downloadUrl).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to download Amazon Adv report from URL: {}", downloadUrl);
                throw new RuntimeException("Failed to download Amazon Adv report");
            }

            return Objects.requireNonNull(response.body()).bytes();
        } catch (IOException e) {
            log.error("Error downloading Amazon Adv report", e);
            throw new RuntimeException("Error downloading Amazon Adv report", e);
        }
    }
}
