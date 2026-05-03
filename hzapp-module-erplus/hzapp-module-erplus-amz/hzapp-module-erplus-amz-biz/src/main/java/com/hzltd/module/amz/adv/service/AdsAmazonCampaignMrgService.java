package com.hzltd.module.amz.adv.service;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.*;

public interface AdsAmazonCampaignMrgService {

    CommonResult<Boolean> updateDynamicBidding(AmzAdvV3CampaignDynamicBiddingUpdateReqVO reqVO);

    CommonResult<Boolean> batchCreateNegativeKeyword(AmzAdvV3CampaignNegativeKeywordBatchCreateReqVO reqVO);

    CommonResult<Boolean> batchDeleteNegativeKeyword(AmzAdvV3CampaignNegativeKeywordBatchDeleteReqVO reqVO);

    CommonResult<Boolean> batchCreateNegativeTargeting(AmzAdvV3CampaignNegativeTargetingBatchCreateReqVO reqVO);

    CommonResult<Boolean> batchDeleteNegativeTargeting(AmzAdvV3CampaignNegativeTargetingBatchDeleteReqVO reqVO);

    void updateCampaign(Long shopId, String campaignId);
}
