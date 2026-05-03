package com.hzltd.module.amz.adv.service.impl;

import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.api.AdsAmazonHelpApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.*;
import com.hzltd.module.amz.adv.service.AmzAdvHelpService;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AmzAdvHelpServiceImpl extends AbstractAmazonAdsService implements AmzAdvHelpService {

    @Resource
    private AdsAmazonHelpApi adsAmazonHelpApi;

    @Override
    public KeywordTargetResponse getKeywordRecommendations(AmzAdvHelpKeywordRecommendationReqVO reqVO) {
        AdsResponse<KeywordTargetResponse> response = adsAmazonHelpApi.getKeywordRecommendations(AdsRequest.of(reqVO.getShopId(), reqVO));
        if (response.isSuccess()) {
            return response.getData();
        }
        throw new RuntimeException("获取关键词推荐失败: " + response.getMessage());
    }

    @Override
    public List<Brand> getNegativeBrandRecommendations(AmzAdvHelpNegativeBrandRecommendationReqVO reqVO) {
        AdsResponse<List<Brand>> response = adsAmazonHelpApi.getNegativeBrandRecommendations(AdsRequest.of(reqVO.getShopId(), reqVO));
        if (response.isSuccess()) {
            return response.getData();
        }
        throw new RuntimeException("获取否定品牌推荐失败: " + response.getMessage());
    }

    @Override
    public TargetableCategories getTargetableCategories(AmzAdvHelpTargetableCategoriesReqVO reqVO) {
        AdsResponse<TargetableCategories> response = adsAmazonHelpApi.getTargetableCategories(AdsRequest.of(reqVO.getShopId(), reqVO));
        if (response.isSuccess()) {
            return response.getData();
        }
        throw new RuntimeException("获取可投放类目失败: " + response.getMessage());
    }

    @Override
    public Object getProductRecommendations(AmzAdvHelpProductRecommendationReqVO reqVO) {
        AdsResponse<Object> response = adsAmazonHelpApi.getProductRecommendations(AdsRequest.of(reqVO.getShopId(), reqVO));
        if (response.isSuccess()) {
            return response.getData();
        }
        throw new RuntimeException("获取产品推荐失败: " + response.getMessage());
    }

    @Override
    public Object getProductMetadata(AmzAdvHelpProductMetadataReqVO reqVO) {
        AdsResponse<Object> response = adsAmazonHelpApi.getProductMetadata(AdsRequest.of(reqVO.getShopId(), reqVO));
        if (response.isSuccess()) {
            return response.getData();
        }
        throw new RuntimeException("获取产品元数据失败: " + response.getMessage());
    }
}
