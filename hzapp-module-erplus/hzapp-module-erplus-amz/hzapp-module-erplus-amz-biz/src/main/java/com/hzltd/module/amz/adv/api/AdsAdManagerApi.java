package com.hzltd.module.amz.adv.api;

import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.sp.api.ProductAdsApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdsAdManagerApi  extends AbstractAmazonAdsService {

    public AdsResponse<List<AdsAdModel>> queryAds(AdsRequest<AdsQueryRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        ProductAdsApi productAdsApi = new ProductAdsApi(getApiClient(authModel));

        SponsoredProductsListSponsoredProductsProductAdsRequestContent content = new SponsoredProductsListSponsoredProductsProductAdsRequestContent();
        if (CollectionUtils.isNotEmpty(request.getRequest().getCampaignIds())) {
            content.campaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
        }
        if (CollectionUtils.isNotEmpty(request.getRequest().getAdGroupIds())) {
            content.adGroupIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getAdGroupIds()));
        }
        if (CollectionUtils.isNotEmpty(request.getRequest().getAdIds())) {
            content.adIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest().getAdIds()));
        }

        try {
            SponsoredProductsListSponsoredProductsProductAdsResponseContent adResp = productAdsApi.listSponsoredProductsProductAds(authModel.getAppKey(), authModel.getProfileId(), content);
            return AdsResponse.success( adResp.getProductAds().stream().map(ad -> map2AdsAdResponse(request.getShopId(), ad)).toList());
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    private AdsAdModel map2AdsAdResponse(Long shopId, SponsoredProductsProductAd productAd) {
        return AdsAdModel.builder()
                .externalId(productAd.getAdId())
                .adGroupExternalId(productAd.getAdGroupId())
                .name(productAd.getAsin())
                .sku(productAd.getSku())
                .asin(productAd.getAsin())
                .status(productAd.getState().getValue())
                .build();
    }

    public AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        ProductAdsApi productAdsApi = new ProductAdsApi(getApiClient(authModel));

        SponsoredProductsUpdateSponsoredProductsProductAdsRequestContent content = new SponsoredProductsUpdateSponsoredProductsProductAdsRequestContent();
        content.addProductAdsItem(new SponsoredProductsUpdateProductAd().adId(request.getRequest().getEntityId()).state(SponsoredProductsCreateOrUpdateEntityState.fromValue(request.getRequest().getStatus())));

        try {
            productAdsApi.updateSponsoredProductsProductAds(authModel.getAppKey(), authModel.getProfileId(), content, "");
            return AdsResponse.success(true);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<List<String>> createAd(AdsRequest<List<AdsAdCreateRequest>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        ProductAdsApi productAdsApi = new ProductAdsApi(getApiClient(authModel));

        try {
            SponsoredProductsCreateSponsoredProductsProductAdsResponseContent response = productAdsApi.createSponsoredProductsProductAds(authModel.getAppKey(), authModel.getProfileId(), null, "");
            if (CollectionUtils.isNotEmpty(response.getProductAds().getSuccess())) {
               return AdsResponse.success(response.getProductAds().getSuccess().stream().map(SponsoredProductsProductAdSuccessResponseItem::getAdId).toList());
            } else {
                log.error("Create Ad Error, request={}, error={}", request.getRequest(), response.getProductAds().getError());
                return AdsResponse.error("Create Ad Error");
            }
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> updateAd(AdsRequest<AdsEntityUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }


}
