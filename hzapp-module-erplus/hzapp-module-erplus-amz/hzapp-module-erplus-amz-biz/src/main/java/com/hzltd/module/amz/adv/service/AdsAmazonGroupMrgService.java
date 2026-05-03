package com.hzltd.module.amz.adv.service;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.adv.api.AdsAdGroupManagerApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.*;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import com.hzltd.module.erplus.adv.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.pojo.CommonResult.error;
import static com.hzltd.framework.common.pojo.CommonResult.success;

@Service
public class AdsAmazonGroupMrgService {

    @Resource
    private AdsAdGroupManagerApi adsAdGroupManagerApi;
    @Resource
    private AdsAdGroupService adGroupService;
    @Resource
    private AdsCampaignService campaignService;

    public CommonResult<Boolean> batchCreateKeyword(AmzAdvV3KeywordBatchCreateReqVO reqVO) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(reqVO.getGroupId());
        if (adGroup == null) {
            return error(400, "广告组不存在");
        }
        AdsCampaignDO campaign = campaignService.getCampaign(adGroup.getCampaignId());
        if (campaign == null) {
            return error(400, "广告计划不存在");
        }

        String amazonAdGroupId = adGroup.getExternalId();
        String amazonCampaignId = campaign.getExternalId();

        // Fill adGroupId and campaignId in keywords
        if (reqVO.getKeywords() != null) {
            reqVO.getKeywords().forEach(k -> {
                k.setAdGroupId(amazonAdGroupId);
                k.setCampaignId(amazonCampaignId);
            });
        }

        AdsRequest<List<SponsoredProductsCreateKeyword>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getKeywords());

        AdsResponse<?> response = adsAdGroupManagerApi.createKeywords(request);
        if (response.isSuccess()) {
            syncKeywordAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return success(true);
        }
        return error(500, response.getMessage());
    }

    public CommonResult<Boolean> batchUpdateKeywordBid(AmzAdvV3KeywordBatchUpdateBidReqVO reqVO) {
        AdsRequest<List<SponsoredProductsUpdateKeyword>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());

        List<SponsoredProductsUpdateKeyword> updateList = reqVO.getItems().stream().map(item -> {
            SponsoredProductsUpdateKeyword keyword = new SponsoredProductsUpdateKeyword();
            keyword.setKeywordId(item.getKeywordId());
            keyword.setBid(item.getBid().doubleValue());
            return keyword;
        }).collect(Collectors.toList());

        request.setRequest(updateList);

        AdsResponse<Boolean> response = adsAdGroupManagerApi.updateKeywords(request);
        if (response.isSuccess()) {
            syncKeywordAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return CommonResult.success(true);
        }
        return error(500, response.getMessage());
    }

    public CommonResult<Boolean> batchDeleteKeyword(AmzAdvV3IdListReqVO reqVO) {
        AdsRequest<List<String>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getIds());

        AdsResponse<Boolean> response = adsAdGroupManagerApi.deleteKeywords(request);
        if (response.isSuccess()) {
            syncKeywordAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return CommonResult.success(true);
        }
        return error(500, response.getMessage());
    }

    public CommonResult<Boolean> batchCreateTargeting(AmzAdvV3TargetingBatchCreateReqVO reqVO) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(reqVO.getGroupId());
        if (adGroup == null) return error(400, "广告组不存在");
        
        AdsCampaignDO campaign = campaignService.getCampaign(adGroup.getCampaignId());
        if (campaign == null) return error(400, "广告计划不存在");

        if (reqVO.getClauses() != null) {
            reqVO.getClauses().forEach(c -> {
                c.setAdGroupId(adGroup.getExternalId());
                c.setCampaignId(campaign.getExternalId());
            });
        }

        AdsRequest<List<SponsoredProductsCreateTargetingClause>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getClauses());

        AdsResponse<?> response = adsAdGroupManagerApi.createKeywordTargets(request);
        if (response.isSuccess()) {
            syncTargetingAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return success(true);
        }
        return error(500, response.getMessage());
    }

    public CommonResult<Boolean> batchUpdateTargetingBid(AmzAdvV3TargetingBatchUpdateBidReqVO reqVO) {
        AdsRequest<List<SponsoredProductsUpdateTargetingClause>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());

        List<SponsoredProductsUpdateTargetingClause> updateList = reqVO.getItems().stream().map(item -> {
            SponsoredProductsUpdateTargetingClause clause = new SponsoredProductsUpdateTargetingClause();
            clause.setTargetId(item.getTargetId());
            clause.setBid(item.getBid().doubleValue());
            return clause;
        }).collect(Collectors.toList());

        request.setRequest(updateList);

        AdsResponse<Boolean> response = adsAdGroupManagerApi.updateKeywordTargets(request);
        if (response.isSuccess()) {
            syncTargetingAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return CommonResult.success(true);
        }
        return error(500, response.getMessage());
    }

    public CommonResult<Boolean> batchDeleteTargeting(AmzAdvV3IdListReqVO reqVO) {
        AdsRequest<List<String>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getIds());

        AdsResponse<Boolean> response = adsAdGroupManagerApi.deleteKeywordTargets(request);
        if (response.isSuccess()) {
            syncTargetingAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return CommonResult.success(true);
        }
        return error(500, response.getMessage());
    }

    // --- Negative Keyword ---

    public CommonResult<Boolean> batchCreateNegativeKeyword(AmzAdvV3NegativeKeywordBatchCreateReqVO reqVO) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(reqVO.getGroupId());
        if (adGroup == null) return error(400, "广告组不存在");
        
        AdsCampaignDO campaign = campaignService.getCampaign(adGroup.getCampaignId());
        if (campaign == null) return error(400, "广告计划不存在");

        if (reqVO.getKeywords() != null) {
            reqVO.getKeywords().forEach(k -> {
                k.setAdGroupId(adGroup.getExternalId());
                k.setCampaignId(campaign.getExternalId());
            });
        }

        AdsRequest<List<SponsoredProductsCreateNegativeKeyword>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getKeywords());

        AdsResponse<?> response = adsAdGroupManagerApi.createNegativeKeywords(request);
        if (response.isSuccess()) {
            syncNegativeKeywordAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return success(true);
        }
        return error(500, response.getMessage());
    }

    public CommonResult<Boolean> batchDeleteNegativeKeyword(AmzAdvV3IdListReqVO reqVO) {
        AdsRequest<List<String>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getIds());

        AdsResponse<Boolean> response = adsAdGroupManagerApi.deleteNegativeKeywords(request);
        if (response.isSuccess()) {
            syncNegativeKeywordAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return CommonResult.success(true);
        }
        return error(200, response.getMessage());
    }

    // --- Negative Targeting ---

    public CommonResult<Boolean> batchCreateNegativeTargeting(AmzAdvV3NegativeTargetingBatchCreateReqVO reqVO) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(reqVO.getGroupId());
        if (adGroup == null) return error(400, "广告组不存在");
        
        AdsCampaignDO campaign = campaignService.getCampaign(adGroup.getCampaignId());
        if (campaign == null) return error(400, "广告计划不存在");

        if (reqVO.getClauses() != null) {
            reqVO.getClauses().forEach(c -> {
                c.setAdGroupId(adGroup.getExternalId());
                c.setCampaignId(campaign.getExternalId());
            });
        }

        AdsRequest<List<SponsoredProductsCreateNegativeTargetingClause>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getClauses());

        AdsResponse<?> response = adsAdGroupManagerApi.createNegativeKeywordTargets(request);
        if (response.isSuccess()) {
            syncNegativeTargetingAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return success(true);
        }
        return error(500, response.getMessage());
    }

    public CommonResult<Boolean> batchDeleteNegativeTargeting(AmzAdvV3IdListReqVO reqVO) {
        AdsRequest<List<String>> request = new AdsRequest<>();
        request.setShopId(reqVO.getShopId());
        request.setRequest(reqVO.getIds());

        AdsResponse<Boolean> response = adsAdGroupManagerApi.deleteNegativeKeywordTargets(request);
        if (response.isSuccess()) {
            syncNegativeTargetingAttributes(reqVO.getShopId(), reqVO.getGroupId());
            return CommonResult.success(true);
        }
        return error(500, response.getMessage());
    }

    // --- Sync Helpers ---

    private void syncKeywordAttributes(Long shopId, Long groupId) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(groupId);
        if (adGroup == null) return;

        AdsResponse<List<SponsoredProductsKeyword>> keywordQueryResp = adsAdGroupManagerApi.queryKeywords(new AdsRequest<AdsQueryRequest>()
                .setShopId(shopId)
                .setRequest(new AdsQueryRequest().setAdGroupIds(List.of(adGroup.getExternalId()))));
        if (keywordQueryResp.isSuccess()) {
            List<SponsoredProductsKeyword> data = keywordQueryResp.getData().stream()
                    .filter(k -> !SponsoredProductsEntityState.ARCHIVED.equals(k.getState()))
                    .collect(Collectors.toList());
            adGroupService.saveOrUpdateAdGroupAttributes(groupId, Map.of(AdsAdGroupManagerApi.KEYWORD, data), false);
        }
    }

    private void syncTargetingAttributes(Long shopId, Long groupId) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(groupId);
        if (adGroup == null) return;

        AdsResponse<List<SponsoredProductsTargetingClause>> targetingQueryResp = adsAdGroupManagerApi.queryTargetsClauses(new AdsRequest<AdsQueryRequest>()
                .setShopId(shopId)
                .setRequest(new AdsQueryRequest().setAdGroupIds(List.of(adGroup.getExternalId()))));
        if (targetingQueryResp.isSuccess()) {
            List<SponsoredProductsTargetingClause> data = targetingQueryResp.getData().stream()
                    .filter(t -> !SponsoredProductsEntityState.ARCHIVED.equals(t.getState()))
                    .collect(Collectors.toList());
            adGroupService.saveOrUpdateAdGroupAttributes(groupId, Map.of(AdsAdGroupManagerApi.TARGET_CLAUSE, data), false);
        }
    }

    private void syncNegativeKeywordAttributes(Long shopId, Long groupId) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(groupId);
        if (adGroup == null) return;

        AdsResponse<List<SponsoredProductsNegativeKeyword>> queryResp = adsAdGroupManagerApi.queryNegativeKeywords(new AdsRequest<AdsQueryRequest>()
                .setShopId(shopId)
                .setRequest(new AdsQueryRequest().setAdGroupIds(List.of(adGroup.getExternalId()))));
        if (queryResp.isSuccess()) {
            List<SponsoredProductsNegativeKeyword> data = queryResp.getData().stream()
                    .filter(k -> !SponsoredProductsEntityState.ARCHIVED.equals(k.getState()))
                    .collect(Collectors.toList());
            adGroupService.saveOrUpdateAdGroupAttributes(groupId, Map.of(AdsAdGroupManagerApi.NEGATIVE_KEYWORD, data), false);
        }
    }

    private void syncNegativeTargetingAttributes(Long shopId, Long groupId) {
        AdsAdGroupDO adGroup = adGroupService.getAdGroup(groupId);
        if (adGroup == null) return;

        AdsResponse<List<SponsoredProductsNegativeTargetingClause>> queryResp = adsAdGroupManagerApi.queryNegativeTargetClauses(new AdsRequest<AdsQueryRequest>()
                .setShopId(shopId)
                .setRequest(new AdsQueryRequest().setAdGroupIds(List.of(adGroup.getExternalId()))));
        if (queryResp.isSuccess()) {
            List<SponsoredProductsNegativeTargetingClause> data = queryResp.getData().stream()
                    .filter(t -> !SponsoredProductsEntityState.ARCHIVED.equals(t.getState()))
                    .collect(Collectors.toList());
            adGroupService.saveOrUpdateAdGroupAttributes(groupId, Map.of(AdsAdGroupManagerApi.NEGATIVE_TARGET_CLAUSE, data), false);
        }
    }
}
