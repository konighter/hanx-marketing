package com.hzltd.module.amz.adv.service;

import com.hzltd.module.adv.api.AdsServiceRegister;
import com.hzltd.module.adv.model.*;
import com.hzltd.module.adv.service.AdsManagerApi;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.api.sp.api.CampaignsApi;
import com.hzltd.module.amz.adv.api.sp.model.*;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.system.enums.AdsPlatformEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AdsServiceRegister(platform = AdsPlatformEnum.AMAZON, serviceClass = AdsManagerApi.class)
public class AdsAmazonManageService extends AbstractAmazonAdsService implements AdsManagerApi {

    @Override
    public AdsResponse<List<AdsCampaignResponse>> queryCampaign(AdsRequest<AdsQueryRequest> request) {
        try {
            Long shopId = request.getShopId();
            AdsQueryRequest queryRequest = request.getRequest();
            AuthorizationModel authModel = getAuthorizationModel(shopId);
            String profileId = authModel.getProfileId();
            CampaignsApi campaignsApi = new CampaignsApi(getApiClient(authModel));

            SponsoredProductsListSponsoredProductsCampaignsRequestContent apiRequest = new SponsoredProductsListSponsoredProductsCampaignsRequestContent();
            if (queryRequest.getCampaignIds() != null && !queryRequest.getCampaignIds().isEmpty()) {
                apiRequest.setCampaignIdFilter(new SponsoredProductsObjectIdFilter()
                        .include(queryRequest.getCampaignIds()));
            }
            if (queryRequest.getCampaignNames() != null && !queryRequest.getCampaignNames().isEmpty()) {
                apiRequest.setNameFilter(new SponsoredProductsNameFilter()
                        .include(queryRequest.getCampaignNames()));
            }

            SponsoredProductsListSponsoredProductsCampaignsResponseContent response = campaignsApi.listSponsoredProductsCampaigns(
                    authModel.getAppKey(),
                    profileId,
                    apiRequest
            );

            if (response == null || response.getCampaigns() == null) {
                return AdsResponse.success(Collections.emptyList());
            }

            List<AdsCampaignResponse> campaigns = response.getCampaigns().stream()
                    .map(this::mapToAdsCampaignResponse)
                    .collect(Collectors.toList());

            return AdsResponse.success(campaigns);
        } catch (Exception e) {
            log.error("Failed to query Amazon campaigns", e);
            return AdsResponse.error(e.getMessage());
        }
    }

    private AdsCampaignResponse mapToAdsCampaignResponse(SponsoredProductsCampaign campaign) {
        AdsCampaignResponse.AdsCampaignResponseBuilder builder = AdsCampaignResponse.builder()
                .externalId(campaign.getCampaignId())
                .name(campaign.getName())
                .campaignType("Sponsored Products")
                .status(campaign.getState().getValue())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .platform(AdsPlatformEnum.AMAZON.name());

        if (campaign.getBudget() != null) {
            builder.dailyBudget(BigDecimal.valueOf(campaign.getBudget().getBudget()));
            builder.budgetType(campaign.getBudget().getBudgetType().getValue());
        }

        if (campaign.getTargetingType() != null) {
            builder.attributes(Map.of("targetingType", campaign.getTargetingType().getValue()));
        }

        return builder.build();
    }

    @Override
    public AdsResponse<Boolean> updateCampaign(AdsRequest<AdsEntityUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<List<AdsAdGroupResponse>> queryAdGroup(AdsRequest<AdsQueryRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateAdGroup(AdsRequest<AdsEntityUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<List<AdsAdResponse>> queryAd(AdsRequest<AdsQueryRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateAd(AdsRequest<AdsEntityUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<List<AdsTargetResponse>> queryTarget(AdsRequest<AdsQueryRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateTarget(AdsRequest<AdsEntityUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateBudget(AdsRequest<AdsBudgetUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    @Override
    public AdsResponse<Boolean> updateBid(AdsRequest<AdsBidUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }
}
