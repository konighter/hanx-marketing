package com.hzltd.module.amz.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.adapter.RefreshTokenCacheAdaptor;
import com.hzltd.module.spapi.model.authorization.AuthorizationModelV0;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class AmzAdvLwaService {

    @Resource
    private RefreshTokenCacheAdaptor localLWAAccessTokenCache;

    private static final String AUTH_ENDPOINT = "https://api.amazon.com/auth/o2/token";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    /**
     * 获取 LWA Access Token
     *
     * @param authModel 授权信息
     * @return Access Token
     */
    public String getAccessToken(AuthorizationModelV0 authModel) {
        String cacheKey = "AMZ_ADV_LWA_" + authModel.getRefreshToken();
        String accessToken = localLWAAccessTokenCache.getCache(cacheKey);
        if (StrUtil.isNotEmpty(accessToken)) {
            return accessToken;
        }

        return refreshAccessToken(authModel, cacheKey);
    }

    private String refreshAccessToken(AuthorizationModelV0 authModel, String cacheKey) {
        log.info("Refreshing Amazon Adv LWA token for shop: {}", authModel.getShopModel().getId());

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", authModel.getRefreshToken())
                .add("client_id", authModel.getAppKey())
                .add("client_secret", authModel.getAppSecret())
                .build();

        Request request = new Request.Builder()
                .url(AUTH_ENDPOINT)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("Failed to refresh Amazon Adv LWA token: {}", responseBody);
                throw new RuntimeException("Failed to refresh Amazon Adv LWA token: " + responseBody);
            }

            JsonNode jsonNode = JsonUtils.parseTree(responseBody);
            String accessToken = jsonNode.get("access_token").asText();
            long expiresIn = jsonNode.get("expires_in").asLong();

            // 缓存 token，提前 5 分钟过期
            localLWAAccessTokenCache.putCache(cacheKey, accessToken, expiresIn - 300);
            return accessToken;
        } catch (IOException e) {
            log.error("Error refreshing Amazon Adv LWA token", e);
            throw new RuntimeException("Error refreshing Amazon Adv LWA token", e);
        }
    }
}
