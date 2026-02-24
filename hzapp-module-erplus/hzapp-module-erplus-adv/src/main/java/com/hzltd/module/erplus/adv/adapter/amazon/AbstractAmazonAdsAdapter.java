package com.hzltd.module.erplus.adv.adapter.amazon;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.module.erplus.adv.adapter.model.AdsTokenResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAmazonProfileMapper;
import com.hzltd.module.infra.dal.dataobject.config.ConfigDO;
import com.hzltd.module.infra.service.config.ConfigService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.ADS_PLATFORM_NOT_SUPPORTED;

@Slf4j
public abstract class AbstractAmazonAdsAdapter {

    protected static final String AMZ_CLIENT_ID = "amz_ads_client_id";
    protected static final String AMZ_CLIENT_SECRET = "amz_ads_client_secret";
    protected static final String AMZ_REDIRECT_URI = "amz_ads_redirect_uri";
    protected static final String AMZ_TOKEN_URL = "https://api.amazon.com/auth/o2/token";

    protected final OkHttpClient httpClient = new OkHttpClient();

    protected final HttpClient client = HttpClient.newHttpClient();

    @Resource
    protected ConfigService configService;

    @Resource
    protected AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Resource
    protected AdsAccountCredentialMapper adsAccountCredentialMapper;

    @Resource
    protected AdsAccountMapper adsAccountMapper;

    protected AdsAccountDO getAdsAccount(Long accountId) {
        return adsAccountMapper.selectById(accountId);
    }

    protected AdsAccountCredentialDO getAdsAccountCredential(Long accountId) {
        AdsAccountDO adsAccount = adsAccountMapper.selectById(accountId);
        if (adsAccount == null || adsAccount.getCredentialId() == null) {
            return null;
        }
        return adsAccountCredentialMapper.selectById(adsAccount.getCredentialId());
    }

    public String getRequiredConfig(String key) {
        return Optional.ofNullable(configService.getConfigByKey(key))
                .map(ConfigDO::getValue)
                .orElseThrow(() -> {
                    log.error("[getRequiredConfig][Amazon] 缺失关键配置: {}", key);
                    return exception(ADS_PLATFORM_NOT_SUPPORTED);
                });
    }



    protected List<AdsAmazonProfileDO> getProfiles(Long accountId) {
        return adsAmazonProfileMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsAmazonProfileDO>()
                        .eq(AdsAmazonProfileDO::getAccountId, accountId));
    }

    protected AdsTokenResult executeTokenRequest(RequestBody body) {
        Request request = new Request.Builder()
                .url(AMZ_TOKEN_URL)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String respBody = response.body().string();
            if (!response.isSuccessful()) {
                log.error("[executeTokenRequest][Amazon] 请求失败, code: {}, body: {}", response.code(), respBody);
                throw new RuntimeException("Amazon Token Request Failed: " + respBody);
            }

            JSONObject json = JSONUtil.parseObj(respBody);
            return AdsTokenResult.builder()
                    .accessToken(json.getStr("access_token"))
                    .refreshToken(json.getStr("refresh_token"))
                    .expiresAt(LocalDateTime.now().plusSeconds(json.getLong("expires_in", 3600L)))
                    .externalAccountId(json.getStr("user_id"))
                    .tokenType(json.getStr("token_type"))
                    .scope(json.getStr("scope"))
                    .build();
        } catch (IOException e) {
            log.error("[executeTokenRequest][Amazon] 网络异常", e);
            throw new RuntimeException("Amazon Token Request Error", e);
        }
    }

    protected <T> T executeGetRequest(AdsAccountCredentialDO credential, String profileId, String url,
            List<Pair<String, String>> params, Function<String, T> callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Amazon-Advertising-API-ClientId", getRequiredConfig(AMZ_CLIENT_ID))
                .addHeader("Authorization", "Bearer " + credential.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .get();
        if (StringUtils.isNotEmpty(profileId)) {
            builder.addHeader("Amazon-Advertising-API-Scope", profileId);
        }

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            String respBody = response.body().string();
            return callback.apply(respBody);
        } catch (IOException e) {
            log.error("[executeGetRequest][Amazon] Url {} 网络异常", url, e);
        }
        return null;
    }


    protected <T> T executeApiPost(AdsAccountCredentialDO credential, String profileId, String url,
                                   List<Pair<String, String>> params, String jsonBody, String contentType, Function<String, T> callback) {


        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Amazon-Advertising-API-ClientId", getRequiredConfig(AMZ_CLIENT_ID))
                .header("Authorization", "Bearer " + credential.getAccessToken())
                .header("Accept", contentType)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (StringUtils.isNotEmpty(profileId)) {
            builder.header("Amazon-Advertising-API-Scope", profileId);
        }

        try {
            HttpResponse<String> resp =  client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                return callback.apply(resp.body());
            } else {
                log.error("[executePostRequest][Amazon] Url {} 异常, message={}", url, resp.body());
                return  null;
            }
        } catch (Exception e) {
            log.error("[executePostRequest][Amazon] Url {} 网络异常", url, e);
        }






        return null;
    }





    protected <T> T executePostequest(AdsAccountCredentialDO credential, String profileId, String url,
            List<Pair<String, String>> params, String jsonBody, Function<String, T> callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Amazon-Advertising-API-ClientId", getRequiredConfig(AMZ_CLIENT_ID))
                .addHeader("Authorization", "Bearer " + credential.getAccessToken())
                .addHeader("Accept", "application/vnd.spCampaign.v3+json")
//                .addHeader("Content-Type", "application/vnd.spCampaign.v3+json")
                .post(RequestBody.create(jsonBody, MediaType.get("application/vnd.spCampaign.v3+json")))
                .addHeader("Content-Type", "application/vnd.spCampaign.v3+json");
        if (StringUtils.isNotEmpty(profileId)) {
            builder.addHeader("Amazon-Advertising-API-Scope", profileId);
        }

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            String respBody = response.body().string();
            return callback.apply(respBody);
        } catch (IOException e) {
            log.error("[executePostequest][Amazon] Url {} 网络异常", url, e);
        }
        return null;
    }

    @Data
    public static class AmzProfileVO {
        String profileId;
        String countryCode;
        String currencyCode;
        String timezone;

        AmzAccountVO accountInfo;
    }

    @Data
    public static class AmzAccountVO {
        // from account
        String accountName;
        String adsAccountId;
        List<String> countryCodes;
        List<Map<String, String>> alternateIds;

        // from profile
        String id;
        String name;
        String marketplaceStringId;
        String type;
        String subType;
    }



}
