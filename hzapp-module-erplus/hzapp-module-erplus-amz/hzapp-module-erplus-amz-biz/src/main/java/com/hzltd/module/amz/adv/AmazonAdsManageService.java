package com.hzltd.module.amz.adv;

import com.hzltd.module.adv.api.AdsServiceRegister;
import com.hzltd.module.adv.model.*;
import com.hzltd.module.adv.service.AdsManagerApi;
import com.hzltd.module.system.enums.AdsPlatformEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AdsServiceRegister(platform = AdsPlatformEnum.AMAZON, serviceClass = AdsManagerApi.class)
public class AmazonAdsManageService implements AdsManagerApi {

    @Override
    public AdsResponse<List<AdsCampaignResponse>> queryCampaign(AdsRequest<AdsQueryRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<Boolean> updateCampaign(AdsRequest<AdsEntityUpdateRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<List<AdsAdGroupResponse>> queryAdGroup(AdsRequest<AdsQueryRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<Boolean> updateAdGroup(AdsRequest<AdsEntityUpdateRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<List<AdsAdResponse>> queryAd(AdsRequest<AdsQueryRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<Boolean> updateAd(AdsRequest<AdsEntityUpdateRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<List<AdsTargetResponse>> queryTarget(AdsRequest<AdsQueryRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<Boolean> updateTarget(AdsRequest<AdsEntityUpdateRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<Boolean> updateBudget(AdsRequest<AdsBudgetUpdateRequest> request) {
        return null;
    }

    @Override
    public AdsResponse<Boolean> updateBid(AdsRequest<AdsBidUpdateRequest> request) {
        return null;
    }
}
