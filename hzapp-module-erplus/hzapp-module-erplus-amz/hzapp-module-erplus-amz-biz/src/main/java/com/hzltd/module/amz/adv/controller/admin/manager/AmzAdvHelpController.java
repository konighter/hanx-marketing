package com.hzltd.module.amz.adv.controller.admin.manager;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.adv.client.sp.model.Brand;
import com.hzltd.module.amz.adv.client.sp.model.KeywordTargetResponse;
import com.hzltd.module.amz.adv.client.sp.model.TargetableCategories;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpKeywordRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpNegativeBrandRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpProductMetadataReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpProductRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpTargetableCategoriesReqVO;
import com.hzltd.module.amz.adv.service.AmzAdvHelpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告辅助接口")
@RestController
@RequestMapping("/erplus/amz/adv/v3/help")
@Validated
public class AmzAdvHelpController {

    @Resource
    private AmzAdvHelpService amzAdvHelpService;

    @PostMapping("/keyword/recommendations")
    @Operation(summary = "获取关键词推荐")
    public CommonResult<KeywordTargetResponse> getKeywordRecommendations(@Valid @RequestBody AmzAdvHelpKeywordRecommendationReqVO reqVO) {
        return success(amzAdvHelpService.getKeywordRecommendations(reqVO));
    }

    @PostMapping("/negative-brand/recommendations")
    @Operation(summary = "获取否定品牌推荐")
    public CommonResult<List<Brand>> getNegativeBrandRecommendations(@Valid @RequestBody AmzAdvHelpNegativeBrandRecommendationReqVO reqVO) {
        return success(amzAdvHelpService.getNegativeBrandRecommendations(reqVO));
    }

    @PostMapping("/targetable-categories")
    @Operation(summary = "获取可投放类目")
    public CommonResult<TargetableCategories> getTargetableCategories(@Valid @RequestBody AmzAdvHelpTargetableCategoriesReqVO reqVO) {
        return success(amzAdvHelpService.getTargetableCategories(reqVO));
    }

    @PostMapping("/product/recommendations")
    @Operation(summary = "获取产品推荐 (Suggested target ASINs)")
    public CommonResult<Object> getProductRecommendations(@Valid @RequestBody AmzAdvHelpProductRecommendationReqVO reqVO) {
        return success(amzAdvHelpService.getProductRecommendations(reqVO));
    }

    @PostMapping("/product/metadata")
    @Operation(summary = "获取产品元数据 (Product metadata)")
    public CommonResult<Object> getProductMetadata(@Valid @RequestBody AmzAdvHelpProductMetadataReqVO reqVO) {
        return success(amzAdvHelpService.getProductMetadata(reqVO));
    }

}
