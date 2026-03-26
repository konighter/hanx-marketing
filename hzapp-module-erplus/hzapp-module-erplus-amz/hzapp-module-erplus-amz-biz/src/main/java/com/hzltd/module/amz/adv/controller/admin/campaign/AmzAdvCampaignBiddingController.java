package com.hzltd.module.amz.adv.controller.admin.campaign;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.adv.dal.dataobject.AmzAdvOptimizationRuleDO;
import com.hzltd.module.amz.adv.service.AmzAdvCampaignBiddingService;
import com.hzltd.module.amz.adv.service.AmzAdvRuleService;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignSaveReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告活动竞价管理与特定配置")
@RestController
@RequestMapping("/erplus/amz/adv/campaign")
@Validated
public class AmzAdvCampaignBiddingController {

    @Resource
    private AmzAdvCampaignBiddingService amzAdvCampaignBiddingService;
    
    @Resource
    private AmzAdvRuleService amzAdvRuleService;

    @PostMapping("/update-bidding")
    @Operation(summary = "更新广告活动特定竞价与位置抢位策略 (Amazon Specific)")
    public CommonResult<Boolean> updateCampaignBiddingAndPlacement(@Valid @RequestBody AmzAdvCampaignSaveReqVO updateReqVO) {
        amzAdvCampaignBiddingService.updateCampaignBiddingAndPlacement(updateReqVO);
        return success(true);
    }
    
    @PostMapping("/rule/save")
    @Operation(summary = "保存亚马逊广告特定 Bid Rule 或 Budget Rule")
    public CommonResult<Boolean> saveOptimizationRule(@Valid @RequestBody AmzAdvOptimizationRuleDO reqVO) {
        amzAdvRuleService.saveOptimizationRule(reqVO);
        return success(true);
    }

}
