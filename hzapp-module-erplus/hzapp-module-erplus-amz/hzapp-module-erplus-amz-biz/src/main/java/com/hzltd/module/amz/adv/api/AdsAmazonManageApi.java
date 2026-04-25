package com.hzltd.module.amz.adv.api;

import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.erplus.adv.api.AdsServiceRegister;
import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.adv.service.AdsManagerApi;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AdsServiceRegister(platform = AdsPlatformEnum.AMAZON, serviceClass = AdsManagerApi.class)
public class AdsAmazonManageApi extends AbstractAmazonAdsService implements AdsManagerApi {

    @Resource
    private AdsCampaignMangerApi adsCampaignMangerApi;
    @Resource
    private AdsAdGroupManagerApi adsAdGroupManagerApi;
    @Resource
    private AdsAdManagerApi adsAdManagerApi;
    @Resource
    private AdsKeyworkManagerApi keyworkManagerApi;

    @Override
    public AdsResponse<List<AdsCampaignModel>> queryCampaign(AdsRequest<AdsQueryRequest> request) {
        return adsCampaignMangerApi.queryCampaign(request);
    }

    @Override
    public AdsResponse<Boolean> updateCampaign(AdsRequest<AdsEntityUpdateRequest> request) {
        return adsCampaignMangerApi.updateCampaign(request);
    }
 
    @Override
    public AdsResponse<String> createCampaign(AdsRequest<AdsCampaignCreateRequest> request) {
        return adsCampaignMangerApi.createCampaign(request);
    }

    @Override
    public AdsResponse<List<AdsAdGroupModel>> queryAdGroup(AdsRequest<AdsQueryRequest> request) {
        return adsAdGroupManagerApi.queryAdGroup(request);
    }

    @Override
    public AdsResponse<Boolean> updateAdGroup(AdsRequest<AdsEntityUpdateRequest> request) {
        return adsAdGroupManagerApi.updateAdGroup(request);
    }
 
    @Override
    public AdsResponse<String> createAdGroup(AdsRequest<AdsAdGroupCreateRequest> request) {
        return adsAdGroupManagerApi.createAdGroup(request);
    }

    @Override
    public AdsResponse<List<AdsAdModel>> queryAd(AdsRequest<AdsQueryRequest> request) {
        return adsAdManagerApi.queryAds(request);
    }

    @Override
    public AdsResponse<Boolean> updateAd(AdsRequest<AdsEntityUpdateRequest> request) {
        return adsAdManagerApi.updateAd(request);
    }
 
    @Override
    public AdsResponse<List<String>> createAd(AdsRequest<List<AdsAdCreateRequest>> request) {
        return adsAdManagerApi.createAd(request);
    }

    @Override
    public AdsResponse<List<AdsTargetModel>> queryTarget(AdsRequest<AdsQueryRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateTarget(AdsRequest<AdsEntityUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AdsStatusUpdateRequest req =  request.getRequest();

        if (AdsEntityTypeEnum.CAMPAIGN.equals(req.getEntityType())) {
            return adsCampaignMangerApi.updateStatus(request);
        } else if (AdsEntityTypeEnum.ADGROUP.equals(req.getEntityType())) {
            return adsAdGroupManagerApi.updateStatus(request);
        } else if (AdsEntityTypeEnum.AD.equals(req.getEntityType())) {
            return adsAdManagerApi.updateStatus(request);
        } else if (AdsEntityTypeEnum.KEYWORD.equals(req.getEntityType())) {
            return adsAdGroupManagerApi.updateKeywordStatus(request);
        }

        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateBudget(AdsRequest<AdsBudgetUpdateRequest> request) {
        AdsBudgetUpdateRequest req =  request.getRequest();
        if (AdsEntityTypeEnum.CAMPAIGN.equals(req.getEntityType())) {
            return adsCampaignMangerApi.updateBudget(request);
        } else {
            return AdsResponse.error("不支持更新预算+["+req.getEntityType().getName()+"]");
        }

    }

    @Override
    public AdsResponse<Boolean> updateBid(AdsRequest<AdsBidUpdateRequest> request) {
        AdsBidUpdateRequest req =  request.getRequest();

        if (AdsEntityTypeEnum.ADGROUP.equals(req.getEntityType())) {
            return AdsResponse.error("不支持修改出价+["+req.getEntityType().getName()+"]");
        } else if (AdsEntityTypeEnum.KEYWORD.equals(req.getEntityType())) {
            return adsAdGroupManagerApi.updateKeywordBid(request);
        } else {
            return AdsResponse.error("不支持修改出价+["+req.getEntityType().getName()+"]");
        }
    }
}
