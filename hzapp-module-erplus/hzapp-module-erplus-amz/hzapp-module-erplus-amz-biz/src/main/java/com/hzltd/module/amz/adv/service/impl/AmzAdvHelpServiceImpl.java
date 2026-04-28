package com.hzltd.module.amz.adv.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.sp.api.KeywordTargetsApi;
import com.hzltd.module.amz.adv.client.sp.api.ProductTargetingApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpKeywordRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpNegativeBrandRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpTargetableCategoriesReqVO;
import com.hzltd.module.amz.adv.service.AmzAdvHelpService;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmzAdvHelpServiceImpl extends AbstractAmazonAdsService implements AmzAdvHelpService {

    @Override
    public KeywordTargetResponse getKeywordRecommendations(AmzAdvHelpKeywordRecommendationReqVO reqVO) {
        AuthorizationModel authModel = getAuthorizationModel(reqVO.getShopId());
        KeywordTargetsApi api = new KeywordTargetsApi(getApiClient(authModel));

        GetRankedKeywordRecommendationRequest request = buildKeywordRecommendationRequest(reqVO);

        try {
            return api.getRankedKeywordRecommendation(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    null,
                    null,
                    request
            );
        } catch (ApiException e) {
            log.error("[AmazonAds] getKeywordRecommendations error: {}", e.getResponseBody(), e);
            throw new RuntimeException("获取关键词推荐失败: " + e.getMessage());
        }
    }

    @Override
    public List<Brand> getNegativeBrandRecommendations(AmzAdvHelpNegativeBrandRecommendationReqVO reqVO) {
        AuthorizationModel authModel = getAuthorizationModel(reqVO.getShopId());
        ProductTargetingApi api = new ProductTargetingApi(getApiClient(authModel));

        try {
            return api.getNegativeBrands(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    null
            );
        } catch (ApiException e) {
            log.error("[AmazonAds] getNegativeBrandRecommendations error: {}", e.getResponseBody(), e);
            throw new RuntimeException("获取否定品牌推荐失败: " + e.getMessage());
        }
    }

    @Override
    public TargetableCategories getTargetableCategories(AmzAdvHelpTargetableCategoriesReqVO reqVO) {
        AuthorizationModel authModel = getAuthorizationModel(reqVO.getShopId());
        ProductTargetingApi api = new ProductTargetingApi(getApiClient(authModel));

        try {
            return api.getTargetableCategories(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    null,
                    reqVO.getLocale()
            );
        } catch (ApiException e) {
            log.error("[AmazonAds] getTargetableCategories error: {}", e.getResponseBody(), e);
            throw new RuntimeException("获取可投放类目失败: " + e.getMessage());
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
