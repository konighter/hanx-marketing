package com.hzltd.module.erplus.controller.admin.amzadv;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.amzadv.service.AmzAdvSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告同步")
@RestController
@RequestMapping("/erplus/amz-adv-sync")
public class AmzAdvSyncController {

    @Resource
    private AmzAdvSyncService amzAdvSyncService;

    @PostMapping("/sync-campaigns")
    @Operation(summary = "同步广告活动")
    public CommonResult<Integer> syncCampaigns(@RequestParam("shopId") String shopId,
                                               @RequestParam("profileId") String profileId) {
        int count = amzAdvSyncService.syncCampaigns(shopId, profileId);
        return success(count);
    }

    @PostMapping("/sync-ad-groups")
    @Operation(summary = "同步广告组")
    public CommonResult<Integer> syncAdGroups(@RequestParam("shopId") String shopId,
                                              @RequestParam("profileId") String profileId) {
        int count = amzAdvSyncService.syncAdGroups(shopId, profileId);
        return success(count);
    }

    @PostMapping("/sync-keywords")
    @Operation(summary = "同步关键词")
    public CommonResult<Integer> syncKeywords(@RequestParam("shopId") String shopId,
                                              @RequestParam("profileId") String profileId) {
        int count = amzAdvSyncService.syncKeywords(shopId, profileId);
        return success(count);
    }

    @PostMapping("/sync-bid-strategies")
    @Operation(summary = "同步出价策略")
    public CommonResult<Integer> syncBidStrategies(@RequestParam("shopId") String shopId,
                                                   @RequestParam("profileId") String profileId) {
        int count = amzAdvSyncService.syncBidStrategies(shopId, profileId);
        return success(count);
    }

    @PostMapping("/sync-all-data")
    @Operation(summary = "全量同步广告数据")
    public CommonResult<Boolean> syncAllData(@RequestParam("shopId") String shopId,
                                             @RequestParam("profileId") String profileId) {
        amzAdvSyncService.syncAllData(shopId, profileId);
        return success(true);
    }

    @PostMapping("/sync-local-campaign-to-amazon/{localCampaignId}")
    @Operation(summary = "同步本地广告活动到亚马逊")
    public CommonResult<Boolean> syncLocalCampaignToAmazon(@RequestParam("shopId") String shopId,
                                                           @RequestParam("profileId") String profileId,
                                                           @PathVariable("localCampaignId") Long localCampaignId) {
        amzAdvSyncService.syncLocalCampaignToAmazon(shopId, profileId, localCampaignId);
        return success(true);
    }

    @PostMapping("/sync-local-adgroup-to-amazon/{localAdGroupId}")
    @Operation(summary = "同步本地广告组到亚马逊")
    public CommonResult<Boolean> syncLocalAdGroupToAmazon(@RequestParam("shopId") String shopId,
                                                          @RequestParam("profileId") String profileId,
                                                          @PathVariable("localAdGroupId") Long localAdGroupId) {
        amzAdvSyncService.syncLocalAdGroupToAmazon(shopId, profileId, localAdGroupId);
        return success(true);
    }

    @PostMapping("/sync-local-keyword-to-amazon/{localKeywordId}")
    @Operation(summary = "同步本地关键词到亚马逊")
    public CommonResult<Boolean> syncLocalKeywordToAmazon(@RequestParam("shopId") String shopId,
                                                          @RequestParam("profileId") String profileId,
                                                          @PathVariable("localKeywordId") Long localKeywordId) {
        amzAdvSyncService.syncLocalKeywordToAmazon(shopId, profileId, localKeywordId);
        return success(true);
    }

    @PostMapping("/sync-local-bidstrategy-to-amazon/{localStrategyId}")
    @Operation(summary = "同步本地出价策略到亚马逊")
    public CommonResult<Boolean> syncLocalBidStrategyToAmazon(@RequestParam("shopId") String shopId,
                                                             @RequestParam("profileId") String profileId,
                                                             @PathVariable("localStrategyId") Long localStrategyId) {
        amzAdvSyncService.syncLocalBidStrategyToAmazon(shopId, profileId, localStrategyId);
        return success(true);
    }
}