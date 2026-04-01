package com.hzltd.module.amz.adv.controller.admin.manager;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvKeywordRespVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvKeywordPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvKeywordSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.amz.service.AmzAdvKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告运营")
@RestController
@RequestMapping("/erplus/amz-adv-operation")
public class AmzAdvKeyworkManagerController {

    @Resource
    private AmzAdvKeywordService amzAdvKeywordService;

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
//        if (vo.getBid() != null && vo.getCampaignId() != null) {
//            // Retrieve campaign to get bidding modifiers (assuming campaignId string in keyword matches campaignId string in campaign)
//            AmzAdvCampaignDO campaignDO = amzAdvCampaignMapper.selectOne(AmzAdvCampaignDO::getCampaignId, vo.getCampaignId());
//            if (campaignDO != null && campaignDO.getDynamicBidding() != null && campaignDO.getDynamicBidding().getPlacementBidding() != null) {
//                for (AmzAdvCampaignDO.PlacementBidding pb : campaignDO.getDynamicBidding().getPlacementBidding()) {
//                    if ("PLACEMENT_TOP_OF_SEARCH".equals(pb.getPlacement()) && pb.getPercentage() != null) {
//                        vo.setCalculatedTopBid(vo.getBid() * (1 + pb.getPercentage() / 100.0));
//                    } else if ("PLACEMENT_PRODUCT_PAGE".equals(pb.getPlacement()) && pb.getPercentage() != null) {
//                        vo.setCalculatedProductPageBid(vo.getBid() * (1 + pb.getPercentage() / 100.0));
//                    }
//                }
//            }
//        }
        return vo;
    }
}
