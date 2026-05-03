package com.hzltd.module.amz.adv.api;

import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.sp.api.*;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.system.annotation.CrossplatformApiLog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdsAdGroupManagerApi extends AbstractAmazonAdsService {
    public static final String KEYWORD = "amz_keyword";
    public static final String TARGET_CLAUSE = "amz_target_clause";
    public static final String NEGATIVE_KEYWORD = "amz_negative_keyword";
    public static final String NEGATIVE_TARGET_CLAUSE = "amz_negative_target_clause";

    @Resource
    private AdsAdGroupService adGroupService;



    public AdsResponse<Boolean> updateAdGroup(AdsRequest<AdsEntityUpdateRequest> request) {
        return AdsResponse.error("Not implemented yet");
    }

    public AdsResponse<String> createAdGroup(AdsRequest<AdsAdGroupCreateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        AdGroupsApi api = new AdGroupsApi(getApiClient(authModel));
        try {
            AdsAdGroupCreateRequest groupCreateRequest = request.getRequest();
            String stateStr = groupCreateRequest.getStatus() != null ? groupCreateRequest.getStatus().toUpperCase() : "ENABLED";
            SponsoredProductsCreateSponsoredProductsAdGroupsRequestContent content = new SponsoredProductsCreateSponsoredProductsAdGroupsRequestContent();
            content.addAdGroupsItem(new SponsoredProductsCreateAdGroup()
                    .campaignId(groupCreateRequest.getCampaignId())
                    .name(groupCreateRequest.getName())
                    .defaultBid(groupCreateRequest.getDefaultBid())
                    .state(SponsoredProductsCreateOrUpdateEntityState.fromValue(stateStr)));
            SponsoredProductsCreateSponsoredProductsAdGroupsResponseContent groupsResponseContent = api.createSponsoredProductsAdGroups(authModel.getAppKey(), authModel.getProfileId(), content, "");

            if (CollectionUtils.isNotEmpty(groupsResponseContent.getAdGroups().getSuccess())) {
                return AdsResponse.success(groupsResponseContent.getAdGroups().getSuccess().get(0).getAdGroupId());
            } else {
                return AdsResponse.error("");
            }



            // 规则?






            //


        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @CrossplatformApiLog
    public AdsResponse<List<AdsAdGroupModel>> queryAdGroup(AdsRequest<AdsQueryRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        AdGroupsApi api = new AdGroupsApi(getApiClient(authModel));
        try {
            SponsoredProductsListSponsoredProductsAdGroupsRequestContent content = new SponsoredProductsListSponsoredProductsAdGroupsRequestContent();
            if (CollectionUtils.isNotEmpty(request.getRequest().getCampaignIds())) {
                content.campaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getAdGroupIds())) {
                content.adGroupIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest().getAdGroupIds()));
            }

            SponsoredProductsListSponsoredProductsAdGroupsResponseContent response = api.listSponsoredProductsAdGroups(authModel.getAppKey(), authModel.getProfileId(), content);
            if (response == null || CollectionUtils.isEmpty(response.getAdGroups())) {
                return AdsResponse.success(Collections.emptyList());
            }

            List<String> adGroupIds = response.getAdGroups().stream().map(SponsoredProductsAdGroup::getAdGroupId).toList();
            AdsRequest<AdsQueryRequest> batchRequest = new AdsRequest<AdsQueryRequest>().setShopId(request.getShopId())
                    .setRequest(new AdsQueryRequest().setAdGroupIds(adGroupIds));

            // 批量获取关键词
            AdsResponse<List<SponsoredProductsKeyword>> keywordResp = this.queryKeywords(batchRequest);
            java.util.Map<String, List<SponsoredProductsKeyword>> groupedKeywords = keywordResp.isSuccess() && CollectionUtils.isNotEmpty(keywordResp.getData())
                    ? keywordResp.getData().stream().collect(Collectors.groupingBy(SponsoredProductsKeyword::getAdGroupId))
                    : Collections.emptyMap();

            // 批量获取定向
            AdsResponse<List<SponsoredProductsTargetingClause>> targetResp = this.queryTargetsClauses(batchRequest);
            java.util.Map<String, List<SponsoredProductsTargetingClause>> groupedTargets = targetResp.isSuccess() && CollectionUtils.isNotEmpty(targetResp.getData())
                    ? targetResp.getData().stream().collect(Collectors.groupingBy(SponsoredProductsTargetingClause::getAdGroupId))
                    : Collections.emptyMap();

            // 批量获取否定关键词
            AdsResponse<List<SponsoredProductsNegativeKeyword>> nKeywordResp = this.queryNegativeKeywords(batchRequest);
            java.util.Map<String, List<SponsoredProductsNegativeKeyword>> groupedNKeywords = nKeywordResp.isSuccess() && CollectionUtils.isNotEmpty(nKeywordResp.getData())
                    ? nKeywordResp.getData().stream().collect(Collectors.groupingBy(SponsoredProductsNegativeKeyword::getAdGroupId))
                    : Collections.emptyMap();

            // 批量获取否定定向
            AdsResponse<List<SponsoredProductsNegativeTargetingClause>> nTargetResp = this.queryNegativeTargetClauses(batchRequest);
            java.util.Map<String, List<SponsoredProductsNegativeTargetingClause>> groupedNTargets = nTargetResp.isSuccess() && CollectionUtils.isNotEmpty(nTargetResp.getData())
                    ? nTargetResp.getData().stream().collect(Collectors.groupingBy(SponsoredProductsNegativeTargetingClause::getAdGroupId))
                    : Collections.emptyMap();

            List<AdsAdGroupModel> adsAdGroupRespons = response.getAdGroups().stream()
                    .map(adGroup -> this.mapToAdsAdGroupResponse(request.getShopId(), adGroup,
                            groupedKeywords.get(adGroup.getAdGroupId()),
                            groupedTargets.get(adGroup.getAdGroupId()),
                            groupedNKeywords.get(adGroup.getAdGroupId()),
                            groupedNTargets.get(adGroup.getAdGroupId())))
                    .toList();
            return AdsResponse.success(adsAdGroupRespons);
        } catch (ApiException e) {
            log.error("[AmazonAds] queryAdGroup error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    private AdsAdGroupModel mapToAdsAdGroupResponse(Long shopId, SponsoredProductsAdGroup adGroup,
                                                   List<SponsoredProductsKeyword> keywords,
                                                   List<SponsoredProductsTargetingClause> targets,
                                                   List<SponsoredProductsNegativeKeyword> nKeywords,
                                                   List<SponsoredProductsNegativeTargetingClause> nTargets) {
        AdsAdGroupModel adGroupResponse = AdsAdGroupModel.builder()
                .externalId(adGroup.getAdGroupId())
                .campaignExternalId(adGroup.getCampaignId())
                .name(adGroup.getName())
                .status(adGroup.getState().getValue())
                .defaultBid(BigDecimal.valueOf(adGroup.getDefaultBid()))
                .build();

        // 广告组定向: 关键词
        if (CollectionUtils.isNotEmpty(keywords)) {
            adGroupResponse.addAttribute(KEYWORD, keywords);
        }

        if (CollectionUtils.isNotEmpty(targets)) {
            adGroupResponse.addAttribute(TARGET_CLAUSE, targets);
        }

        // 广告组否定定向
        if (CollectionUtils.isNotEmpty(nKeywords)) {
            adGroupResponse.addAttribute(NEGATIVE_KEYWORD, nKeywords);
        }

        if (CollectionUtils.isNotEmpty(nTargets)) {
            adGroupResponse.addAttribute(NEGATIVE_TARGET_CLAUSE, nTargets);
        }

        return adGroupResponse;
    }


    /**
     * 更新广告组状态
     * @param request
     * @return
     */
    public AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        AdGroupsApi api = new AdGroupsApi(getApiClient(authModel));

        try {
            SponsoredProductsUpdateSponsoredProductsAdGroupsRequestContent content = new SponsoredProductsUpdateSponsoredProductsAdGroupsRequestContent();
            content.setAdGroups(List.of(new SponsoredProductsUpdateAdGroup().adGroupId(request.getRequest().getEntityId()).state(SponsoredProductsCreateOrUpdateEntityState.fromValue(request.getRequest().getStatus()))));

            api.updateSponsoredProductsAdGroups(authModel.getAppKey(), authModel.getProfileId(), content, "");
            return AdsResponse.success(true);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }


    // Keywords CRUD
    @CrossplatformApiLog
    public AdsResponse<List<SponsoredProductsKeyword>> queryKeywords(AdsRequest<AdsQueryRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordsApi keywordsApi = new KeywordsApi(getApiClient(authModel));
        try {
            SponsoredProductsListSponsoredProductsKeywordsRequestContent content = new SponsoredProductsListSponsoredProductsKeywordsRequestContent();

            if (CollectionUtils.isNotEmpty(request.getRequest().getCampaignIds())) {
                content.campaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getAdGroupIds())) {
                content.adGroupIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getAdGroupIds()));
            }

            if (CollectionUtils.isNotEmpty(request.getRequest().getTargetIds())) {
                content.keywordIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest().getTargetIds()));
            }

            SponsoredProductsListSponsoredProductsKeywordsResponseContent response = keywordsApi.listSponsoredProductsKeywords(authModel.getAppKey(), authModel.getProfileId(), content);

            return AdsResponse.success(response.getKeywords());
        } catch (ApiException e) {
            log.error("Query keywords error: profileId={}, request={}", authModel.getProfileId(), request, e);
            return AdsResponse.error(e.getResponseBody());
        }
    }


    @CrossplatformApiLog
    public AdsResponse<List<SponsoredProductsKeyword>> createKeywords(AdsRequest<List<SponsoredProductsCreateKeyword>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordsApi keywordsApi = new KeywordsApi(getApiClient(authModel));
        try {
            SponsoredProductsCreateSponsoredProductsKeywordsRequestContent content = new SponsoredProductsCreateSponsoredProductsKeywordsRequestContent();
            content.setKeywords(request.getRequest());
            SponsoredProductsCreateSponsoredProductsKeywordsResponseContent response = keywordsApi.createSponsoredProductsKeywords(authModel.getAppKey(), authModel.getProfileId(), content, "return=representation");
            
            if (response != null && response.getKeywords() != null && response.getKeywords().getSuccess() != null) {
                List<SponsoredProductsKeyword> keywords = response.getKeywords().getSuccess().stream()
                        .map(SponsoredProductsKeywordSuccessResponseItem::getKeyword)
                        .collect(Collectors.toList());
                return AdsResponse.success(keywords);
            }
            return AdsResponse.success(Collections.emptyList());
        } catch (ApiException e) {
            log.error("[AmazonAds] createKeywords error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }


    @CrossplatformApiLog
    public AdsResponse<Boolean> updateKeywordBid(AdsRequest<AdsBidUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordsApi keywordsApi = new KeywordsApi(getApiClient(authModel));
        try {
            AdsBidUpdateRequest update = request.getRequest();
            SponsoredProductsUpdateKeyword updateItem = new SponsoredProductsUpdateKeyword();
            updateItem.setKeywordId(update.getEntityId());
            updateItem.setBid(request.getRequest().getBid().doubleValue());
            SponsoredProductsUpdateSponsoredProductsKeywordsRequestContent content = new SponsoredProductsUpdateSponsoredProductsKeywordsRequestContent();
            content.setKeywords(Collections.singletonList(updateItem));

            SponsoredProductsUpdateSponsoredProductsKeywordsResponseContent response = keywordsApi.updateSponsoredProductsKeywords(authModel.getAppKey(), authModel.getProfileId(), content, null);
            boolean success = response != null && response.getKeywords() != null && response.getKeywords().getSuccess() != null && !response.getKeywords().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Update keyword status error: profileId={}, keywordIds={}", authModel.getProfileId(), request.getRequest().getEntityId(), e);
            return AdsResponse.error(e.getResponseBody());
        }


    }

    public AdsResponse<Boolean> updateKeywords(AdsRequest<List<SponsoredProductsUpdateKeyword>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordsApi keywordsApi = new KeywordsApi(getApiClient(authModel));
        try {
            SponsoredProductsUpdateSponsoredProductsKeywordsRequestContent content = new SponsoredProductsUpdateSponsoredProductsKeywordsRequestContent();
            content.setKeywords(request.getRequest());

            SponsoredProductsUpdateSponsoredProductsKeywordsResponseContent response = keywordsApi.updateSponsoredProductsKeywords(authModel.getAppKey(), authModel.getProfileId(), content, null);
            boolean success = response != null && response.getKeywords() != null && response.getKeywords().getSuccess() != null && !response.getKeywords().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Update keywords error: profileId={}, request={}", authModel.getProfileId(), request.getRequest(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }


    @CrossplatformApiLog
    public AdsResponse<Boolean> updateKeywordStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordsApi keywordsApi = new KeywordsApi(getApiClient(authModel));
        try {
            AdsStatusUpdateRequest update = request.getRequest();
            SponsoredProductsUpdateKeyword updateItem = new SponsoredProductsUpdateKeyword();
            updateItem.setKeywordId(update.getEntityId());
            updateItem.setState(SponsoredProductsCreateOrUpdateEntityState.fromValue(update.getStatus().toUpperCase()));

            SponsoredProductsUpdateSponsoredProductsKeywordsRequestContent content = new SponsoredProductsUpdateSponsoredProductsKeywordsRequestContent();
            content.setKeywords(Collections.singletonList(updateItem));

            SponsoredProductsUpdateSponsoredProductsKeywordsResponseContent response = keywordsApi.updateSponsoredProductsKeywords(authModel.getAppKey(), authModel.getProfileId(), content, null);
            boolean success = response != null && response.getKeywords() != null && response.getKeywords().getSuccess() != null && !response.getKeywords().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Update keyword status error: profileId={}, keywordIds={}", authModel.getProfileId(), request.getRequest().getEntityId(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }


    @CrossplatformApiLog
    public AdsResponse<Boolean> deleteKeywords(AdsRequest<List<String>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordsApi keywordsApi = new KeywordsApi(getApiClient(authModel));
        try {
            SponsoredProductsDeleteSponsoredProductsKeywordsRequestContent content = new SponsoredProductsDeleteSponsoredProductsKeywordsRequestContent();
            content.setKeywordIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest()));
            SponsoredProductsDeleteSponsoredProductsKeywordsResponseContent response = keywordsApi.deleteSponsoredProductsKeywords(authModel.getAppKey(), authModel.getProfileId(), content);
            boolean success = response != null && response.getKeywords() != null && response.getKeywords().getSuccess() != null && !response.getKeywords().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Delete keywords error: profileId={}, keywordIds={}", authModel.getProfileId(), request.getRequest(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    // Negative Keywords CRUD
    public AdsResponse<List<SponsoredProductsNegativeKeyword>> queryNegativeKeywords(AdsRequest<AdsQueryRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeKeywordsApi api = new NegativeKeywordsApi(getApiClient(authModel));
        try {
            SponsoredProductsListSponsoredProductsNegativeKeywordsRequestContent content = new SponsoredProductsListSponsoredProductsNegativeKeywordsRequestContent();
            if (CollectionUtils.isNotEmpty(request.getRequest().getCampaignIds())) {
                content.campaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getAdGroupIds())) {
                content.adGroupIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getAdGroupIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getTargetIds())) {
                content.negativeKeywordIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest().getKeywordIds()));
            }

            SponsoredProductsListSponsoredProductsNegativeKeywordsResponseContent response = api.listSponsoredProductsNegativeKeywords(authModel.getAppKey(), authModel.getProfileId(), content);
            return AdsResponse.success(response.getNegativeKeywords());
        } catch (ApiException e) {
            log.error("[AmazonAds] queryNegativeKeywords error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<List<SponsoredProductsNegativeKeyword>> createNegativeKeywords(AdsRequest<List<SponsoredProductsCreateNegativeKeyword>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeKeywordsApi api = new NegativeKeywordsApi(getApiClient(authModel));
        try {
            SponsoredProductsCreateSponsoredProductsNegativeKeywordsRequestContent content = new SponsoredProductsCreateSponsoredProductsNegativeKeywordsRequestContent();
            content.setNegativeKeywords(request.getRequest());
            SponsoredProductsCreateSponsoredProductsNegativeKeywordsResponseContent response = api.createSponsoredProductsNegativeKeywords(authModel.getAppKey(), authModel.getProfileId(), content, "return=representation");
            
            if (response != null && response.getNegativeKeywords() != null && CollectionUtils.isNotEmpty(response.getNegativeKeywords().getSuccess())) {
                List<SponsoredProductsNegativeKeyword> negativeKeywords = response.getNegativeKeywords().getSuccess().stream()
                        .map(SponsoredProductsNegativeKeywordSuccessResponseItem::getNegativeKeyword)
                        .collect(Collectors.toList());
                return AdsResponse.success(negativeKeywords);
            }
            return AdsResponse.success(Collections.emptyList());
        } catch (ApiException e) {
            log.error("[AmazonAds] createNegativeKeywords error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> updateNegativeKeywordStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeKeywordsApi api = new NegativeKeywordsApi(getApiClient(authModel));
        try {
            AdsStatusUpdateRequest update = request.getRequest();
            SponsoredProductsUpdateNegativeKeyword updateItem = new SponsoredProductsUpdateNegativeKeyword();
            updateItem.setKeywordId(update.getEntityId());
            updateItem.setState(SponsoredProductsCreateOrUpdateEntityState.fromValue(update.getStatus().toUpperCase()));

            SponsoredProductsUpdateSponsoredProductsNegativeKeywordsRequestContent content = new SponsoredProductsUpdateSponsoredProductsNegativeKeywordsRequestContent();
            content.setNegativeKeywords(Collections.singletonList(updateItem));

            SponsoredProductsUpdateSponsoredProductsNegativeKeywordsResponseContent response = api.updateSponsoredProductsNegativeKeywords(authModel.getAppKey(), authModel.getProfileId(), content, null);
            boolean success = response != null && response.getNegativeKeywords() != null && response.getNegativeKeywords().getSuccess() != null && !response.getNegativeKeywords().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("[AmazonAds] updateNegativeKeywordStatus error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> deleteNegativeKeywords(AdsRequest<List<String>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeKeywordsApi api = new NegativeKeywordsApi(getApiClient(authModel));
        try {
            SponsoredProductsDeleteSponsoredProductsNegativeKeywordsRequestContent content = new SponsoredProductsDeleteSponsoredProductsNegativeKeywordsRequestContent();
            content.negativeKeywordIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest()));
            SponsoredProductsDeleteSponsoredProductsNegativeKeywordsResponseContent response = api.deleteSponsoredProductsNegativeKeywords(authModel.getAppKey(), authModel.getProfileId(), content);
            boolean success = response != null && response.getNegativeKeywords() != null && response.getNegativeKeywords().getSuccess() != null && !response.getNegativeKeywords().getSuccess().isEmpty();
            if (success) {
                return AdsResponse.success(success);
            } else {
                return AdsResponse.error(response.getNegativeKeywords().getError().toString());
            }

        } catch (ApiException e) {
            log.error("[AmazonAds] deleteNegativeKeywords error: {}", e.getResponseBody(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    // Keyword Targets CRUD
    public AdsResponse<List<SponsoredProductsTargetingClause>> queryTargetsClauses(AdsRequest<AdsQueryRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        TargetingClausesApi api = new TargetingClausesApi(getApiClient(authModel));
        try {
            SponsoredProductsListSponsoredProductsTargetingClausesRequestContent content = new SponsoredProductsListSponsoredProductsTargetingClausesRequestContent();
            if (CollectionUtils.isNotEmpty(request.getRequest().getCampaignIds())) {
                content.campaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getAdGroupIds())) {
                content.adGroupIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getAdGroupIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getTargetIds())) {
                content.targetIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest().getTargetIds()));
            }

            SponsoredProductsListSponsoredProductsTargetingClausesResponseContent response = api.listSponsoredProductsTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content);
            return AdsResponse.success(response.getTargetingClauses());
        } catch (ApiException e) {
            log.error("Query keyword targets error: profileId={}, request={}", authModel.getProfileId(), request, e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<List<SponsoredProductsTargetingClause>> createKeywordTargets(AdsRequest<List<SponsoredProductsCreateTargetingClause>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        TargetingClausesApi api = new TargetingClausesApi(getApiClient(authModel));
        try {
            SponsoredProductsCreateSponsoredProductsTargetingClausesRequestContent content = new SponsoredProductsCreateSponsoredProductsTargetingClausesRequestContent();
            content.setTargetingClauses(request.getRequest());
            SponsoredProductsCreateSponsoredProductsTargetingClausesResponseContent response = api.createSponsoredProductsTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content, "return=representation");
            
            if (response != null && response.getTargetingClauses() != null && response.getTargetingClauses().getSuccess() != null) {
                List<SponsoredProductsTargetingClause> targets = response.getTargetingClauses().getSuccess().stream()
                        .map(SponsoredProductsTargetingClauseSuccessResponseItem::getTargetingClause)
                        .collect(Collectors.toList());
                return AdsResponse.success(targets);
            }
            return AdsResponse.success(Collections.emptyList());
        } catch (ApiException e) {
            log.error("Create keyword targets error: profileId={}, request={}", authModel.getProfileId(), request, e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> updateKeywordTargetStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        TargetingClausesApi api = new TargetingClausesApi(getApiClient(authModel));
        try {
            AdsStatusUpdateRequest update = request.getRequest();
            SponsoredProductsUpdateTargetingClause updateItem = new SponsoredProductsUpdateTargetingClause();
            updateItem.setTargetId(update.getEntityId());
            updateItem.setState(SponsoredProductsCreateOrUpdateEntityState.fromValue(update.getStatus().toUpperCase()));

            SponsoredProductsUpdateSponsoredProductsTargetingClausesRequestContent content = new SponsoredProductsUpdateSponsoredProductsTargetingClausesRequestContent();
            content.setTargetingClauses(Collections.singletonList(updateItem));

            SponsoredProductsUpdateSponsoredProductsTargetingClausesResponseContent response = api.updateSponsoredProductsTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content, null);
            boolean success = response != null && response.getTargetingClauses() != null && response.getTargetingClauses().getSuccess() != null && !response.getTargetingClauses().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Update keyword target status error: profileId={}, request={}", authModel.getProfileId(), request.getRequest(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> updateKeywordTargets(AdsRequest<List<SponsoredProductsUpdateTargetingClause>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        TargetingClausesApi api = new TargetingClausesApi(getApiClient(authModel));
        try {
            SponsoredProductsUpdateSponsoredProductsTargetingClausesRequestContent content = new SponsoredProductsUpdateSponsoredProductsTargetingClausesRequestContent();
            content.setTargetingClauses(request.getRequest());

            SponsoredProductsUpdateSponsoredProductsTargetingClausesResponseContent response = api.updateSponsoredProductsTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content, null);
            boolean success = response != null && response.getTargetingClauses() != null && response.getTargetingClauses().getSuccess() != null && !response.getTargetingClauses().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Update keyword targets error: profileId={}, request={}", authModel.getProfileId(), request.getRequest(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> deleteKeywordTargets(AdsRequest<List<String>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        TargetingClausesApi api = new TargetingClausesApi(getApiClient(authModel));
        try {
            SponsoredProductsDeleteSponsoredProductsTargetingClausesRequestContent content = new SponsoredProductsDeleteSponsoredProductsTargetingClausesRequestContent();
            content.setTargetIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest()));
            SponsoredProductsDeleteSponsoredProductsTargetingClausesResponseContent response = api.deleteSponsoredProductsTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content);
            boolean success = response != null && response.getTargetingClauses() != null && response.getTargetingClauses().getSuccess() != null && !response.getTargetingClauses().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Delete keyword targets error: profileId={}, request={}", authModel.getProfileId(), request.getRequest(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    // Negative Keyword Targets CRUD
    public AdsResponse<List<SponsoredProductsNegativeTargetingClause>> queryNegativeTargetClauses(AdsRequest<AdsQueryRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeTargetingClausesApi api = new NegativeTargetingClausesApi(getApiClient(authModel));
        try {
            SponsoredProductsListSponsoredProductsNegativeTargetingClausesRequestContent content = new SponsoredProductsListSponsoredProductsNegativeTargetingClausesRequestContent();
            if (CollectionUtils.isNotEmpty(request.getRequest().getCampaignIds())) {
                content.campaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getAdGroupIds())) {
                content.adGroupIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getAdGroupIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getTargetIds())) {
                content.negativeTargetIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest().getTargetIds()));
            }

            SponsoredProductsListSponsoredProductsNegativeTargetingClausesResponseContent response = api.listSponsoredProductsNegativeTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content);
            return AdsResponse.success(response.getNegativeTargetingClauses());
        } catch (ApiException e) {
            log.error("Query negative keyword targets error: profileId={}, request={}", authModel.getProfileId(), request, e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<List<SponsoredProductsNegativeTargetingClause>> createNegativeKeywordTargets(AdsRequest<List<SponsoredProductsCreateNegativeTargetingClause>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeTargetingClausesApi api = new NegativeTargetingClausesApi(getApiClient(authModel));
        try {
            SponsoredProductsCreateSponsoredProductsNegativeTargetingClausesRequestContent content = new SponsoredProductsCreateSponsoredProductsNegativeTargetingClausesRequestContent();
            content.setNegativeTargetingClauses(request.getRequest());
            SponsoredProductsCreateSponsoredProductsNegativeTargetingClausesResponseContent response = api.createSponsoredProductsNegativeTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content, "return=representation");
            
            if (response != null && response.getNegativeTargetingClauses() != null && response.getNegativeTargetingClauses().getSuccess() != null) {
                List<SponsoredProductsNegativeTargetingClause> targets = response.getNegativeTargetingClauses().getSuccess().stream()
                        .map(SponsoredProductsNegativeTargetingClauseSuccessResponseItem::getNegativeTargetingClause)
                        .collect(Collectors.toList());
                return AdsResponse.success(targets);
            }
            return AdsResponse.success(Collections.emptyList());
        } catch (ApiException e) {
            log.error("Create negative keyword targets error: profileId={}, request={}", authModel.getProfileId(), request, e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> updateNegativeKeywordTargetStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeTargetingClausesApi api = new NegativeTargetingClausesApi(getApiClient(authModel));
        try {
            AdsStatusUpdateRequest update = request.getRequest();
            SponsoredProductsUpdateNegativeTargetingClause updateItem = new SponsoredProductsUpdateNegativeTargetingClause();
            updateItem.setTargetId(update.getEntityId());
            updateItem.setState(SponsoredProductsCreateOrUpdateEntityState.fromValue(update.getStatus().toUpperCase()));

            SponsoredProductsUpdateSponsoredProductsNegativeTargetingClausesRequestContent content = new SponsoredProductsUpdateSponsoredProductsNegativeTargetingClausesRequestContent();
            content.setNegativeTargetingClauses(Collections.singletonList(updateItem));

            SponsoredProductsUpdateSponsoredProductsNegativeTargetingClausesResponseContent response = api.updateSponsoredProductsNegativeTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content, null);
            boolean success = response != null && response.getNegativeTargetingClauses() != null && response.getNegativeTargetingClauses().getSuccess() != null && !response.getNegativeTargetingClauses().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Update negative keyword target status error: profileId={}, request={}", authModel.getProfileId(), request.getRequest(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> deleteNegativeKeywordTargets(AdsRequest<List<String>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        NegativeTargetingClausesApi api = new NegativeTargetingClausesApi(getApiClient(authModel));
        try {
            SponsoredProductsDeleteSponsoredProductsNegativeTargetingClausesRequestContent content = new SponsoredProductsDeleteSponsoredProductsNegativeTargetingClausesRequestContent();
            content.negativeTargetIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest()));
            SponsoredProductsDeleteSponsoredProductsNegativeTargetingClausesResponseContent response = api.deleteSponsoredProductsNegativeTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content);
            boolean success = response != null && response.getNegativeTargetingClauses() != null && response.getNegativeTargetingClauses().getSuccess() != null && !response.getNegativeTargetingClauses().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Delete negative keyword targets error: profileId={}, request={}", authModel.getProfileId(), request.getRequest(), e);
            return AdsResponse.error(e.getResponseBody());
        }
    }
    @CrossplatformApiLog
    public AdsResponse<List<String>> createTargets(AdsRequest<List<AdsTargetModel>> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        KeywordsApi keywordApi = new KeywordsApi(getApiClient(authModel));
        NegativeKeywordsApi negativeKeywordApi = new NegativeKeywordsApi(getApiClient(authModel));
        TargetingClausesApi targetingClausesApi = new TargetingClausesApi(getApiClient(authModel));
        NegativeTargetingClausesApi negativeTargetingClausesApi = new NegativeTargetingClausesApi(getApiClient(authModel));

        List<String> allExternalIds = new java.util.ArrayList<>();
        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder();

        try {
            // Group by type
            List<AdsTargetModel> keywords = request.getRequest().stream()
                    .filter(t -> t.getAdEntityType() == AdsEntityTypeEnum.KEYWORD).collect(Collectors.toList());
            List<AdsTargetModel> negativeKeywords = request.getRequest().stream()
                    .filter(t -> t.getAdEntityType() == AdsEntityTypeEnum.NEGATIVE_KEYWORD).collect(Collectors.toList());
            List<AdsTargetModel> targetingClauses = request.getRequest().stream()
                    .filter(t -> t.getAdEntityType() == AdsEntityTypeEnum.TARGET).collect(Collectors.toList());
            List<AdsTargetModel> negativeTargetingClauses = request.getRequest().stream()
                    .filter(t -> t.getAdEntityType() == AdsEntityTypeEnum.NEGATIVE_TARGET).collect(Collectors.toList());

            // 1. Keywords
            if (CollectionUtils.isNotEmpty(keywords)) {
                SponsoredProductsCreateSponsoredProductsKeywordsRequestContent content = new SponsoredProductsCreateSponsoredProductsKeywordsRequestContent();
                for (AdsTargetModel kw : keywords) {
                    content.addKeywordsItem(new SponsoredProductsCreateKeyword()
                            .campaignId(kw.getCampaignExternalId())
                            .adGroupId(kw.getAdEntityId())
                            .keywordText((String) kw.getAttributes().get("keywordText"))
                            .matchType(SponsoredProductsCreateOrUpdateMatchType.fromValue(((String) kw.getAttributes().get("matchType")).toUpperCase()))
                            .bid(kw.getAttributes().get("bid") != null ? ((Number) kw.getAttributes().get("bid")).doubleValue() : null)
                            .state(SponsoredProductsCreateOrUpdateEntityState.ENABLED));
                }
                SponsoredProductsCreateSponsoredProductsKeywordsResponseContent resp = keywordApi.createSponsoredProductsKeywords(authModel.getAppKey(), authModel.getProfileId(), content, "");
                if (resp.getKeywords() != null) {
                    if (CollectionUtils.isNotEmpty(resp.getKeywords().getSuccess())) {
                        resp.getKeywords().getSuccess().forEach(s -> allExternalIds.add(s.getKeywordId()));
                    }
                    if (CollectionUtils.isNotEmpty(resp.getKeywords().getError())) {
                        hasError = true;
                        resp.getKeywords().getError().forEach(e -> errorMessage.append("Keyword Error: ").append(e.getErrors()).append("; "));
                    }
                }
            }

            // 2. Negative Keywords
            if (CollectionUtils.isNotEmpty(negativeKeywords)) {
                SponsoredProductsCreateSponsoredProductsNegativeKeywordsRequestContent content = new SponsoredProductsCreateSponsoredProductsNegativeKeywordsRequestContent();
                for (AdsTargetModel nkw : negativeKeywords) {
                    content.addNegativeKeywordsItem(new SponsoredProductsCreateNegativeKeyword()
                            .campaignId(nkw.getCampaignExternalId())
                            .adGroupId(nkw.getAdEntityId())
                            .keywordText((String) nkw.getAttributes().get("keywordText"))
                            .matchType(SponsoredProductsCreateOrUpdateNegativeMatchType.fromValue(((String) nkw.getAttributes().get("matchType")).toUpperCase()))
                            .state(SponsoredProductsCreateOrUpdateEntityState.ENABLED));
                }
                SponsoredProductsCreateSponsoredProductsNegativeKeywordsResponseContent resp = negativeKeywordApi.createSponsoredProductsNegativeKeywords(authModel.getAppKey(), authModel.getProfileId(), content, "");
                if (resp.getNegativeKeywords() != null) {
                    if (CollectionUtils.isNotEmpty(resp.getNegativeKeywords().getSuccess())) {
                        resp.getNegativeKeywords().getSuccess().forEach(s -> allExternalIds.add(s.getNegativeKeywordId()));
                    }
                    if (CollectionUtils.isNotEmpty(resp.getNegativeKeywords().getError())) {
                        hasError = true;
                        resp.getNegativeKeywords().getError().forEach(e -> errorMessage.append("NegKeyword Error: ").append(e.getErrors()).append("; "));
                    }
                }
            }

            // 3. Targeting Clauses
            if (CollectionUtils.isNotEmpty(targetingClauses)) {
                SponsoredProductsCreateSponsoredProductsTargetingClausesRequestContent content = new SponsoredProductsCreateSponsoredProductsTargetingClausesRequestContent();
                for (AdsTargetModel tc : targetingClauses) {
                    List<SponsoredProductsCreateTargetingExpressionPredicate> predicates = new java.util.ArrayList<>();
                    List<java.util.Map<String, String>> expression = (List<java.util.Map<String, String>>) tc.getAttributes().get("expression");
                    if (CollectionUtils.isNotEmpty(expression)) {
                        for (java.util.Map<String, String> exp : expression) {
                            predicates.add(new SponsoredProductsCreateTargetingExpressionPredicate()
                                    .type(SponsoredProductsCreateTargetingExpressionPredicateType.fromValue(exp.get("type")))
                                    .value(exp.get("value")));
                        }
                    }
                    content.addTargetingClausesItem(new SponsoredProductsCreateTargetingClause()
                            .campaignId(tc.getCampaignExternalId())
                            .adGroupId(tc.getAdEntityId())
                            .expression(predicates)
                            .expressionType(SponsoredProductsCreateExpressionType.fromValue(((String) tc.getAttributes().get("expressionType")).toUpperCase()))
                            .bid(tc.getAttributes().get("bid") != null ? ((Number) tc.getAttributes().get("bid")).doubleValue() : null)
                            .state(SponsoredProductsCreateOrUpdateEntityState.ENABLED));
                }
                SponsoredProductsCreateSponsoredProductsTargetingClausesResponseContent resp = targetingClausesApi.createSponsoredProductsTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content, "");
                if (resp.getTargetingClauses() != null) {
                    if (CollectionUtils.isNotEmpty(resp.getTargetingClauses().getSuccess())) {
                        resp.getTargetingClauses().getSuccess().forEach(s -> allExternalIds.add(s.getTargetId()));
                    }
                    if (CollectionUtils.isNotEmpty(resp.getTargetingClauses().getError())) {
                        hasError = true;
                        resp.getTargetingClauses().getError().forEach(e -> errorMessage.append("Targeting Error: ").append(e.getErrors()).append("; "));
                    }
                }
            }

            // 4. Negative Targeting Clauses
            if (CollectionUtils.isNotEmpty(negativeTargetingClauses)) {
                SponsoredProductsCreateSponsoredProductsNegativeTargetingClausesRequestContent content = new SponsoredProductsCreateSponsoredProductsNegativeTargetingClausesRequestContent();
                for (AdsTargetModel ntc : negativeTargetingClauses) {
                    List<SponsoredProductsCreateOrUpdateNegativeTargetingExpressionPredicate> predicates = new java.util.ArrayList<>();
                    List<java.util.Map<String, String>> expression = (List<java.util.Map<String, String>>) ntc.getAttributes().get("expression");
                    if (CollectionUtils.isNotEmpty(expression)) {
                        for (java.util.Map<String, String> exp : expression) {
                            predicates.add(new SponsoredProductsCreateOrUpdateNegativeTargetingExpressionPredicate()
                                    .type(SponsoredProductsCreateOrUpdateNegativeTargetingExpressionPredicateType.fromValue(exp.get("type")))
                                    .value(exp.get("value")));
                        }
                    }
                    content.addNegativeTargetingClausesItem(new SponsoredProductsCreateNegativeTargetingClause()
                            .campaignId(ntc.getCampaignExternalId())
                            .adGroupId(ntc.getAdEntityId())
                            .expression(predicates)
                            .state(SponsoredProductsCreateOrUpdateEntityState.ENABLED));
                }
                SponsoredProductsCreateSponsoredProductsNegativeTargetingClausesResponseContent resp = negativeTargetingClausesApi.createSponsoredProductsNegativeTargetingClauses(authModel.getAppKey(), authModel.getProfileId(), content, "");
                if (resp.getNegativeTargetingClauses() != null) {
                    if (CollectionUtils.isNotEmpty(resp.getNegativeTargetingClauses().getSuccess())) {
                        resp.getNegativeTargetingClauses().getSuccess().forEach(s -> allExternalIds.add(s.getTargetId()));
                    }
                    if (CollectionUtils.isNotEmpty(resp.getNegativeTargetingClauses().getError())) {
                        hasError = true;
                        resp.getNegativeTargetingClauses().getError().forEach(e -> errorMessage.append("NegTargeting Error: ").append(e.getErrors()).append("; "));
                    }
                }
            }

            if (hasError) {
                return AdsResponse.error(errorMessage.toString());
            }
            return AdsResponse.success(allExternalIds);

        } catch (ApiException e) {
            log.error("[createTargets] Amazon API Error: {}", e.getResponseBody(), e);
            return AdsResponse.error("Amazon API Error: " + e.getResponseBody());
        } catch (Exception e) {
            log.error("[createTargets] Internal Error", e);
            return AdsResponse.error("Internal Error: " + e.getMessage());
        }
    }
}
