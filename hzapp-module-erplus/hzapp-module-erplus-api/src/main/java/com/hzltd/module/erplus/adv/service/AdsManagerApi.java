package com.hzltd.module.erplus.adv.service;

import com.hzltd.module.erplus.adv.model.*;

import java.util.List;

/**
 * 广告实体更新接口: 按Field更新实体，不支持整体更新，降低递进结构的复杂度
 */
public interface AdsManagerApi {

    AdsResponse<List<AdsCampaignModel>> queryCampaign(AdsRequest<AdsQueryRequest> request);

    /**
     * Create campaign
     * @param request
     * @return externalId
     */
    AdsResponse<String> createCampaign(AdsRequest<AdsCampaignCreateRequest> request);

    /**
     * Add/Del/Update
     * @param request
     * @return
     */
    AdsResponse<Boolean> updateCampaign(AdsRequest<AdsEntityUpdateRequest> request);

    AdsResponse<List<AdsAdGroupModel>> queryAdGroup(AdsRequest<AdsQueryRequest> request);

    /**
     * Add/Del/Update
     * @param request
     * @return
     */
    AdsResponse<Boolean> updateAdGroup(AdsRequest<AdsEntityUpdateRequest> request);

    /**
     * Create adGroup
     * @param request
     * @return externalId
     */
    AdsResponse<String> createAdGroup(AdsRequest<AdsAdGroupCreateRequest> request);

    AdsResponse<List<AdsAdModel>> queryAd(AdsRequest<AdsQueryRequest> request);

    /**
     * Add/Del/Update ad
     * @param request
     * @return
     */
    AdsResponse<Boolean> updateAd(AdsRequest<AdsEntityUpdateRequest> request);

    /**
     * Create ad
     * @param request
     * @return externalId
     */
    AdsResponse<List<String>> createAd(AdsRequest<List<AdsAdCreateRequest>> request);

    AdsResponse<List<AdsTargetModel>> queryTarget(AdsRequest<AdsQueryRequest> request);

    AdsResponse<Boolean> updateTarget(AdsRequest<AdsEntityUpdateRequest> request);

    AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request);

    AdsResponse<Boolean> updateBudget(AdsRequest<AdsBudgetUpdateRequest> request);

    AdsResponse<Boolean> updateBid(AdsRequest<AdsBidUpdateRequest> request);

}
