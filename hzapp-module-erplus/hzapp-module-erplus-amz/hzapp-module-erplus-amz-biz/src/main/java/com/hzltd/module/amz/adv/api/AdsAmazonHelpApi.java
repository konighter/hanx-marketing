package com.hzltd.module.amz.adv.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.client.ApiClient;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.sp.api.KeywordTargetsApi;
import com.hzltd.module.amz.adv.client.sp.api.ProductRecommendationServiceApi;
import com.hzltd.module.amz.adv.client.sp.api.ProductTargetingApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.*;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdsAmazonHelpApi extends AbstractAmazonAdsService {

    public AdsResponse<KeywordTargetResponse> getKeywordRecommendations(AdsRequest<AmzAdvHelpKeywordRecommendationReqVO> request) {
        AmzAdvHelpKeywordRecommendationReqVO reqVO = request.getRequest();
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordTargetsApi api = new KeywordTargetsApi(getApiClient(authModel));

        GetRankedKeywordRecommendationRequest amazonRequest = buildKeywordRecommendationRequest(reqVO);

        try {
            KeywordTargetResponse response = api.getRankedKeywordRecommendation(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    null,
                    null,
                    amazonRequest
            );
            return AdsResponse.success(response);
        } catch (ApiException e) {
            log.error("[AmazonAds] getKeywordRecommendations error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<List<Brand>> getNegativeBrandRecommendations(AdsRequest<AmzAdvHelpNegativeBrandRecommendationReqVO> request) {
        AmzAdvHelpNegativeBrandRecommendationReqVO reqVO = request.getRequest();
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        ProductTargetingApi api = new ProductTargetingApi(getApiClient(authModel));

        try {
            List<Brand> response = api.getNegativeBrands(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    reqVO.getKeyword()
            );
            return AdsResponse.success(response);
        } catch (ApiException e) {
            log.error("[AmazonAds] getNegativeBrandRecommendations error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<TargetableCategories> getTargetableCategories(AdsRequest<AmzAdvHelpTargetableCategoriesReqVO> request) {
        AmzAdvHelpTargetableCategoriesReqVO reqVO = request.getRequest();
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        ProductTargetingApi api = new ProductTargetingApi(getApiClient(authModel));

        try {
            TargetableCategories response = api.getTargetableCategories(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    null,
                    reqVO.getLocale()
            );
            return AdsResponse.success(response);
        } catch (ApiException e) {
            log.error("[AmazonAds] getTargetableCategories error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Object> getProductRecommendations(AdsRequest<AmzAdvHelpProductRecommendationReqVO> request) {
        AmzAdvHelpProductRecommendationReqVO reqVO = request.getRequest();
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        ProductRecommendationServiceApi api = new ProductRecommendationServiceApi(getApiClient(authModel));

        GetProductRecommendationsRequest amazonRequest = new GetProductRecommendationsRequest();
        amazonRequest.setAdAsins(reqVO.getAdAsins());
        amazonRequest.setCount(reqVO.getCount());
        amazonRequest.setCursor(reqVO.getCursor());
        amazonRequest.setLocale(reqVO.getLocale());

        try {
            Object response = api.getProductRecommendations(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    authModel.getAdsAccountId(),
                    amazonRequest
            );
            return AdsResponse.success(response);
        } catch (ApiException e) {
            log.error("[AmazonAds] getProductRecommendations error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Object> getProductMetadata(AdsRequest<AmzAdvHelpProductMetadataReqVO> request) {
        AmzAdvHelpProductMetadataReqVO reqVO = request.getRequest();
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        ApiClient apiClient = getApiClient(authModel);

        Map<String, Object> body = new HashMap<>();
        body.put("adType", reqVO.getAdType());
        body.put("asins", reqVO.getAsins());
        body.put("skus", reqVO.getSkus());
        body.put("searchStr", reqVO.getSearchStr());
        body.put("checkEligibility", reqVO.getCheckEligibility());
        body.put("checkItemDetails", reqVO.getCheckItemDetails());
        body.put("pageIndex", reqVO.getPageIndex());
        body.put("pageSize", reqVO.getPageSize());
        body.put("sortBy", reqVO.getSortBy());
        body.put("sortOrder", reqVO.getSortOrder());
        body.put("locale", reqVO.getLocale());

        try {
            String jsonBody = apiClient.getObjectMapper().writeValueAsString(body);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(apiClient.getBaseUri() + "/product/metadata"))
                    .header("Content-Type", "application/vnd.productmetadatarequest.v1+json")
                    .header("Accept", "application/vnd.productmetadataresponse.v1+json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            // Apply interceptor for auth headers
            if (apiClient.getRequestInterceptor() != null) {
                apiClient.getRequestInterceptor().accept(requestBuilder);
            }

            HttpResponse<InputStream> response = apiClient.getHttpClient().send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            if (response.statusCode() / 100 != 2) {
                InputStream errorStream = ApiClient.getResponseBody(response);
                String errorBody = errorStream == null ? "" : new String(errorStream.readAllBytes());
                log.error("[AmazonAds] getProductMetadata error: {} - {}", response.statusCode(), errorBody);
                return AdsResponse.error("获取产品元数据失败: " + response.statusCode() + " " + errorBody);
            }

            InputStream responseBody = ApiClient.getResponseBody(response);
            if (responseBody == null) return AdsResponse.success(null);

            Object result = apiClient.getObjectMapper().readValue(responseBody, Object.class);
            return AdsResponse.success(result);

        } catch (Exception e) {
            log.error("[AmazonAds] getProductMetadata error", e);
            return AdsResponse.error(e.getMessage());
        }
    }

    private GetRankedKeywordRecommendationRequest buildKeywordRecommendationRequest(AmzAdvHelpKeywordRecommendationReqVO reqVO) {
        GetRankedKeywordRecommendationRequest request = new GetRankedKeywordRecommendationRequest();

        if ("KEYWORDS_FOR_ADGROUP".equals(reqVO.getRecommendationType())) {
            AdGroupKeywordTargetRankRecommendationRequest adGroupReq = new AdGroupKeywordTargetRankRecommendationRequest();
            adGroupReq.setAdGroupId(reqVO.getAdGroupId());
            adGroupReq.setCampaignId(reqVO.getCampaignId());
            adGroupReq.setRecommendationType(AdGroupKeywordTargetRankRecommendationRequest.RecommendationTypeEnum.KEYWORDS_FOR_ADGROUP);

            if (reqVO.getMaxRecommendations() != null) {
                adGroupReq.setMaxRecommendations(new BigDecimal(reqVO.getMaxRecommendations()));
            }
            if (StrUtil.isNotBlank(reqVO.getSortDimension())) {
                adGroupReq.setSortDimension(AdGroupKeywordTargetRankRecommendationRequest.SortDimensionEnum.fromValue(reqVO.getSortDimension()));
            }
            if (StrUtil.isNotBlank(reqVO.getLocale())) {
                adGroupReq.setLocale(AdGroupKeywordTargetRankRecommendationRequest.LocaleEnum.fromValue(reqVO.getLocale()));
            }
            if (CollUtil.isNotEmpty(reqVO.getTargets())) {
                adGroupReq.setTargets(buildTargets(reqVO.getTargets()));
            }
            request.setActualInstance(adGroupReq);
        } else {
            AsinsKeywordTargetRankRecommendationRequest asinsReq = new AsinsKeywordTargetRankRecommendationRequest();
            asinsReq.setAsins(reqVO.getAsins());
            asinsReq.setRecommendationType(AsinsKeywordTargetRankRecommendationRequest.RecommendationTypeEnum.KEYWORDS_FOR_ASINS);

            if (reqVO.getMaxRecommendations() != null) {
                asinsReq.setMaxRecommendations(new BigDecimal(reqVO.getMaxRecommendations()));
            }
            if (StrUtil.isNotBlank(reqVO.getSortDimension())) {
                asinsReq.setSortDimension(AsinsKeywordTargetRankRecommendationRequest.SortDimensionEnum.fromValue(reqVO.getSortDimension()));
            }
            if (StrUtil.isNotBlank(reqVO.getLocale())) {
                asinsReq.setLocale(AsinsKeywordTargetRankRecommendationRequest.LocaleEnum.fromValue(reqVO.getLocale()));
            }
            if (CollUtil.isNotEmpty(reqVO.getTargets())) {
                asinsReq.setTargets(buildTargets(reqVO.getTargets()));
            }
            request.setActualInstance(asinsReq);
        }

        return request;
    }

    private List<KeywordTargetRankRecommendationRequestAllOfTargets> buildTargets(List<String> keywords) {
        return keywords.stream().map(t -> {
            KeywordTargetRankRecommendationRequestAllOfTargets target = new KeywordTargetRankRecommendationRequestAllOfTargets();
            target.setKeyword(t);
            target.setMatchType(KeywordTargetRankRecommendationRequestAllOfTargets.MatchTypeEnum.BROAD);
            return target;
        }).collect(Collectors.toList());
    }
}
