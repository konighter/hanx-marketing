package com.hzltd.module.amz.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvKeywordRespVO;
import com.hzltd.module.amz.controller.admin.vo.*;
import com.hzltd.module.amz.dal.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.amz.service.AmzAdvAdGroupService;
import com.hzltd.module.amz.service.AmzAdvBidStrategyService;
import com.hzltd.module.amz.service.AmzAdvCampaignService;
import com.hzltd.module.amz.service.AmzAdvKeywordService;
import com.hzltd.module.amz.dal.mapper.AmzAdvCampaignMapper;
import com.hzltd.framework.common.util.object.BeanUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告运营")
@RestController
@RequestMapping("/erplus/amz-adv-operation")
public class AmzAdvOperationController {

    @Resource
    private AmzAdvCampaignService amzAdvCampaignService;
    @Resource
    private AmzAdvAdGroupService amzAdvAdGroupService;
    @Resource
    private AmzAdvKeywordService amzAdvKeywordService;
    @Resource
    private AmzAdvBidStrategyService amzAdvBidStrategyService;
    @Resource
    private AmzAdvCampaignMapper amzAdvCampaignMapper;

    // ========== 广告活动管理 ==========
    
    @PostMapping("/campaign/create")
    @Operation(summary = "创建广告活动")
    public CommonResult<Long> createCampaign(@RequestBody @Valid AmzAdvCampaignSaveReqVO createReqVO) {
        return success(amzAdvCampaignService.createAmzAdvCampaign(createReqVO));
    }

    @PutMapping("/campaign/update")
    @Operation(summary = "更新广告活动")
    public CommonResult<Boolean> updateCampaign(@RequestBody @Valid AmzAdvCampaignSaveReqVO updateReqVO) {
        amzAdvCampaignService.updateAmzAdvCampaign(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/campaign/delete/{id}")
    @Operation(summary = "删除广告活动")
    public CommonResult<Boolean> deleteCampaign(@PathVariable("id") Long id) {
        amzAdvCampaignService.deleteAmzAdvCampaign(id);
        return success(true);
    }

    @GetMapping("/campaign/get/{id}")
    @Operation(summary = "获取广告活动")
    public CommonResult<AmzAdvCampaignDO> getCampaign(@PathVariable("id") Long id) {
        return success(amzAdvCampaignService.getAmzAdvCampaign(id));
    }

    @GetMapping("/campaign/page")
    @Operation(summary = "分页获取广告活动")
    public CommonResult<PageResult<AmzAdvCampaignDO>> getCampaignPage(@Valid AmzAdvCampaignPageReqVO pageReqVO) {
        return success(amzAdvCampaignService.getAmzAdvCampaignPage(pageReqVO));
    }

    // ========== 广告组管理 ==========
    
    @PostMapping("/ad-group/create")
    @Operation(summary = "创建广告组")
    public CommonResult<Long> createAdGroup(@RequestBody @Valid AmzAdvAdGroupSaveReqVO createReqVO) {
        return success(amzAdvAdGroupService.createAmzAdvAdGroup(createReqVO));
    }

    @PutMapping("/ad-group/update")
    @Operation(summary = "更新广告组")
    public CommonResult<Boolean> updateAdGroup(@RequestBody @Valid AmzAdvAdGroupSaveReqVO updateReqVO) {
        amzAdvAdGroupService.updateAmzAdvAdGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/ad-group/delete/{id}")
    @Operation(summary = "删除广告组")
    public CommonResult<Boolean> deleteAdGroup(@PathVariable("id") Long id) {
        amzAdvAdGroupService.deleteAmzAdvAdGroup(id);
        return success(true);
    }

    @GetMapping("/ad-group/get/{id}")
    @Operation(summary = "获取广告组")
    public CommonResult<AmzAdvAdGroupDO> getAdGroup(@PathVariable("id") Long id) {
        return success(amzAdvAdGroupService.getAmzAdvAdGroup(id));
    }

    @GetMapping("/ad-group/page")
    @Operation(summary = "分页获取广告组")
    public CommonResult<PageResult<AmzAdvAdGroupDO>> getAdGroupPage(@Valid AmzAdvAdGroupPageReqVO pageReqVO) {
        return success(amzAdvAdGroupService.getAmzAdvAdGroupPage(pageReqVO));
    }

    // ========== 关键词管理 ==========
    
    @PostMapping("/keyword/create")
    @Operation(summary = "创建关键词")
    public CommonResult<Long> createKeyword(@RequestBody @Valid AmzAdvKeywordSaveReqVO createReqVO) {
        return success(amzAdvKeywordService.createAmzAdvKeyword(createReqVO));
    }

    @PutMapping("/keyword/update")
    @Operation(summary = "更新关键词")
    public CommonResult<Boolean> updateKeyword(@RequestBody @Valid AmzAdvKeywordSaveReqVO updateReqVO) {
        amzAdvKeywordService.updateAmzAdvKeyword(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/keyword/delete/{id}")
    @Operation(summary = "删除关键词")
    public CommonResult<Boolean> deleteKeyword(@PathVariable("id") Long id) {
        amzAdvKeywordService.deleteAmzAdvKeyword(id);
        return success(true);
    }

    @GetMapping("/keyword/get/{id}")
    @Operation(summary = "获取关键词")
    public CommonResult<AmzAdvKeywordRespVO> getKeyword(@PathVariable("id") Long id) {
        AmzAdvKeywordDO keywordDO = amzAdvKeywordService.getAmzAdvKeyword(id);
        return success(buildKeywordRespVO(keywordDO));
    }

    @GetMapping("/keyword/page")
    @Operation(summary = "分页获取关键词")
    public CommonResult<PageResult<AmzAdvKeywordRespVO>> getKeywordPage(@Valid AmzAdvKeywordPageReqVO pageReqVO) {
        PageResult<AmzAdvKeywordDO> pageResult = amzAdvKeywordService.getAmzAdvKeywordPage(pageReqVO);
        List<AmzAdvKeywordRespVO> voList = pageResult.getList().stream()
                .map(this::buildKeywordRespVO)
                .toList();
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    private AmzAdvKeywordRespVO buildKeywordRespVO(AmzAdvKeywordDO keywordDO) {
        if (keywordDO == null) return null;
        AmzAdvKeywordRespVO vo = BeanUtils.toBean(keywordDO, AmzAdvKeywordRespVO.class);
        if (vo.getBid() != null && vo.getCampaignId() != null) {
            // Retrieve campaign to get bidding modifiers (assuming campaignId string in keyword matches campaignId string in campaign)
            AmzAdvCampaignDO campaignDO = amzAdvCampaignMapper.selectOne(AmzAdvCampaignDO::getCampaignId, vo.getCampaignId());
            if (campaignDO != null && campaignDO.getDynamicBidding() != null && campaignDO.getDynamicBidding().getPlacementBidding() != null) {
                for (AmzAdvCampaignDO.PlacementBidding pb : campaignDO.getDynamicBidding().getPlacementBidding()) {
                    if ("PLACEMENT_TOP_OF_SEARCH".equals(pb.getPlacement()) && pb.getPercentage() != null) {
                        vo.setCalculatedTopBid(vo.getBid() * (1 + pb.getPercentage() / 100.0));
                    } else if ("PLACEMENT_PRODUCT_PAGE".equals(pb.getPlacement()) && pb.getPercentage() != null) {
                        vo.setCalculatedProductPageBid(vo.getBid() * (1 + pb.getPercentage() / 100.0));
                    }
                }
            }
        }
        return vo;
    }

    // ========== 出价策略管理 ==========
    
    @PostMapping("/bid-strategy/create")
    @Operation(summary = "创建出价策略")
    public CommonResult<Long> createBidStrategy(@RequestBody @Valid AmzAdvBidStrategySaveReqVO createReqVO) {
        return success(amzAdvBidStrategyService.createAmzAdvBidStrategy(createReqVO));
    }

    @PutMapping("/bid-strategy/update")
    @Operation(summary = "更新出价策略")
    public CommonResult<Boolean> updateBidStrategy(@RequestBody @Valid AmzAdvBidStrategySaveReqVO updateReqVO) {
        amzAdvBidStrategyService.updateAmzAdvBidStrategy(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/bid-strategy/delete/{id}")
    @Operation(summary = "删除出价策略")
    public CommonResult<Boolean> deleteBidStrategy(@PathVariable("id") Long id) {
        amzAdvBidStrategyService.deleteAmzAdvBidStrategy(id);
        return success(true);
    }

    @GetMapping("/bid-strategy/get/{id}")
    @Operation(summary = "获取出价策略")
    public CommonResult<AmzAdvBidStrategyDO> getBidStrategy(@PathVariable("id") Long id) {
        return success(amzAdvBidStrategyService.getAmzAdvBidStrategy(id));
    }

    @GetMapping("/bid-strategy/page")
    @Operation(summary = "分页获取出价策略")
    public CommonResult<PageResult<AmzAdvBidStrategyDO>> getBidStrategyPage(@Valid AmzAdvBidStrategyPageReqVO pageReqVO) {
        return success(amzAdvBidStrategyService.getAmzAdvBidStrategyPage(pageReqVO));
    }
}