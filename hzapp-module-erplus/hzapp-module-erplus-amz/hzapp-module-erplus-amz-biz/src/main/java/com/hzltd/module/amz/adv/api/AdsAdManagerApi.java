package com.hzltd.module.amz.adv.api;

import com.hzltd.module.adv.model.*;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.sp.api.ProductAdsApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
