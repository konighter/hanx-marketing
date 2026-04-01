package com.hzltd.module.amz.adv.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.service.AmzAdvSyncService;
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

}