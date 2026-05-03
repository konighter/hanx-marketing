package com.hzltd.module.amz.adv.service.impl;

import com.hzltd.framework.common.exception.ErrorCode;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.adv.api.AdsCampaignMangerApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.*;
import com.hzltd.module.amz.adv.service.AdsAmazonCampaignMrgService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import com.hzltd.module.erplus.adv.model.AdsCampaignModel;
import com.hzltd.module.erplus.adv.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdsAmazonCampaignMrgServiceImpl implements AdsAmazonCampaignMrgService {

    @Resource
    private AdsCampaignMangerApi campaignMangerApi;
    @Resource
    private AdsCampaignService campaignService;
    @Lazy
    @Resource
    private AdsAmazonCampaignMrgService self;


    @Override
    public CommonResult<Boolean> updateDynamicBidding(AmzAdvV3CampaignDynamicBiddingUpdateReqVO reqVO) {
        AdsCampaignDO campaign = campaignService.getCampaign(reqVO.getCampaignId());
        if (campaign == null) {
            return CommonResult.error(new ErrorCode(0, "广告不存在"));
        }

        SponsoredProductsUpdateCampaign campaignUpdate = new SponsoredProductsUpdateCampaign()
                .campaignId(campaign.getExternalId())
                .dynamicBidding(reqVO.getDynamicBidding());

        campaignUpdate.setSiteRestrictions(null);
        campaignUpdate.setTags(null);

        AdsResponse<Boolean> response = campaignMangerApi.updateCampaign(AdsRequest.of(reqVO.getShopId(), campaignUpdate));

        // 修改完, 主动更新本地数据
        if (response.isSuccess()) {
            self.updateCampaign(reqVO.getShopId(), campaign.getExternalId());
        }

        return CommonResult.success(response.getData());
    }

    @Override
    public CommonResult<Boolean> batchCreateNegativeKeyword(AmzAdvV3CampaignNegativeKeywordBatchCreateReqVO reqVO) {
        AdsCampaignDO campaign = campaignService.getCampaign(reqVO.getCampaignId());
        if (campaign == null) {
            return CommonResult.error(new ErrorCode(0, "广告不存在"));
        }

        if (reqVO.getKeywords() != null) {
            reqVO.getKeywords().forEach(kw -> kw.setCampaignId(campaign.getExternalId()));
        }

        AdsResponse<?> response = campaignMangerApi.createCampaignNegativeKeywords(AdsRequest.of(reqVO.getShopId(), reqVO.getKeywords()));

        // 修改完, 主动更新本地数据
        if (response.isSuccess()) {
            self.updateCampaign(reqVO.getShopId(), campaign.getExternalId());
        }
        return CommonResult.success(response.isSuccess());
    }

    @Override
    public CommonResult<Boolean> batchDeleteNegativeKeyword(AmzAdvV3CampaignNegativeKeywordBatchDeleteReqVO reqVO) {
        AdsCampaignDO campaign = campaignService.getCampaign(reqVO.getCampaignId());
        if (campaign == null) {
            return CommonResult.error(new ErrorCode(0, "广告不存在"));
        }

        AdsResponse<Boolean> response = campaignMangerApi.deleteCampaignNegativeKeywords(AdsRequest.of(reqVO.getShopId(), reqVO.getKeywords()));

        if (response.isSuccess()) {
            self.updateCampaign(reqVO.getShopId(), campaign.getExternalId());
        }
        return CommonResult.success(response.getData());
    }

    @Override
    public CommonResult<Boolean> batchCreateNegativeTargeting(AmzAdvV3CampaignNegativeTargetingBatchCreateReqVO reqVO) {
        AdsCampaignDO campaign = campaignService.getCampaign(reqVO.getCampaignId());
        if (campaign == null) {
            return CommonResult.error(new ErrorCode(0, "广告不存在"));
        }

        if (reqVO.getClauses() != null) {
            reqVO.getClauses().forEach(clause -> clause.setCampaignId(campaign.getExternalId()));
        }

        AdsResponse<?> response = campaignMangerApi.createCampaignNegativeTargetingClauses(AdsRequest.of(reqVO.getShopId(), reqVO.getClauses()));

        // 修改完, 主动更新本地数据
        if (response.isSuccess()) {
            self.updateCampaign(reqVO.getShopId(), campaign.getExternalId());
        }

        return CommonResult.success(response.isSuccess());
    }

    @Override
    public CommonResult<Boolean> batchDeleteNegativeTargeting(AmzAdvV3CampaignNegativeTargetingBatchDeleteReqVO reqVO) {
        AdsCampaignDO campaign = campaignService.getCampaign(reqVO.getCampaignId());
        if (campaign == null) {
            return CommonResult.error(new ErrorCode(0, "广告不存在"));
        }
        AdsResponse<Boolean> response = campaignMangerApi.deleteCampaignNegativeTargetingClauses(AdsRequest.of(reqVO.getShopId(), reqVO.getIds()));
        if (response.isSuccess()) {
            self.updateCampaign(reqVO.getShopId(), campaign.getExternalId());
        }

        return CommonResult.success(response.getData());
    }

    @Async
    @Override
    public void updateCampaign(Long shopId, String campaignId) {
        log.info("[updateCampaign] shopId = {}, campaignId={}", shopId, campaignId);
        AdsResponse<List<AdsCampaignModel>> campaignQueryResult =  campaignMangerApi.queryCampaign(AdsRequest.of(shopId, new AdsQueryRequest().setCampaignIds(List.of(campaignId))));
        if (campaignQueryResult.isSuccess()) {
            campaignQueryResult.getData().forEach(campaignModel -> {
                campaignService.saveCampaign(shopId, campaignModel);
            });
        }
    }
}
