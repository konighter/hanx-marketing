package com.hzltd.module.adv.service;

import com.hzltd.module.adv.model.*;

import java.util.List;

/**
 * 广告实体更新接口: 按Field更新实体，不支持整体更新，降低递进结构的复杂度
 */
public interface AdsManagerApi {

    AdsResponse<List<AdsCampaignResponse>> queryCampaign(AdsRequest<AdsQueryRequest> request);

    /**
     * Add/Del/Update
     * @param request
     * @return
     */
    AdsResponse<Boolean> updateCampaign(AdsRequest<AdsEntityUpdateRequest> request);

    AdsResponse<List<AdsAdGroupResponse>> queryAdGroup(AdsRequest<AdsQueryRequest> request);

    /**
     * Add/Del/Update
     * @param request
     * @return
     */
    AdsResponse<Boolean> updateAdGroup(AdsRequest<AdsEntityUpdateRequest> request);

    AdsResponse<List<AdsAdResponse>> queryAd(AdsRequest<AdsQueryRequest> request);

    /**
     * Add/Del/Update ad
     * @param request
     * @return
     */
    AdsResponse<Boolean> updateAd(AdsRequest<AdsEntityUpdateRequest> request);

    AdsResponse<List<AdsTargetResponse>> queryTarget(AdsRequest<AdsQueryRequest> request);

    AdsResponse<Boolean> updateTarget(AdsRequest<AdsEntityUpdateRequest> request);

    AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request);

    AdsResponse<Boolean> updateBudget(AdsRequest<AdsBudgetUpdateRequest> request);

    AdsResponse<Boolean> updateBid(AdsRequest<AdsBidUpdateRequest> request);

}
