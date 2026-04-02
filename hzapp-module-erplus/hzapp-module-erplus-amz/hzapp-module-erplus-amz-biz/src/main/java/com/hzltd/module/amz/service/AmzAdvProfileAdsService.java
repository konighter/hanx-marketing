package com.hzltd.module.amz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AmzAdvProfileAdsService extends AbstractAmazonAdsService {

    private final OkHttpClient client = new OkHttpClient();

    /**
     * 获取广告账户的 Profiles
     *
     * @param apiRequest 请求对象
     * @return Profile 列表的 JsonNode
     */
    public List<String> listProfiles(ApiRequest<?> apiRequest) {
        String url = getApiEndpoint(apiRequest.getMarketId()) + "/v2/profiles";
        Request request = newAuthenticatedRequestBuilder(apiRequest, url, null).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to list Amazon Adv profiles: {}", responseBody);
                throw new RuntimeException("Failed to list Amazon Adv profiles: " + responseBody);
            }

            JsonNode profiles = JsonUtils.parseTree(responseBody);
            List<String> profileIds = new ArrayList<>();
            if (profiles.isArray()) {
                for (JsonNode profile : profiles) {
                    profileIds.add(profile.get("profileId").asText());
                }
            }
            return profileIds;
        } catch (IOException e) {
            log.error("Error listing Amazon Adv profiles", e);
            throw new RuntimeException("Error listing Amazon Adv profiles", e);
        }
    }
}
