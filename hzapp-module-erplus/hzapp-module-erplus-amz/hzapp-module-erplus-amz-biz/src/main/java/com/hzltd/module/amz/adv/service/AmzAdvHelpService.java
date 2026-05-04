package com.hzltd.module.amz.adv.service;

import com.hzltd.module.amz.adv.client.sp.model.Brand;
import com.hzltd.module.amz.adv.client.sp.model.KeywordTargetResponse;
import com.hzltd.module.amz.adv.client.sp.model.TargetableCategories;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpKeywordRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpNegativeBrandRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpProductMetadataReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpProductRecommendationReqVO;
import com.hzltd.module.amz.adv.controller.admin.manager.vo.AmzAdvHelpTargetableCategoriesReqVO;

import java.util.List;

/**
 * 亚马逊广告辅助服务接口
 */
public interface AmzAdvHelpService {

    /**
     * 获取关键词推荐
     */
    List<KeywordTargetResponse> getKeywordRecommendations(AmzAdvHelpKeywordRecommendationReqVO reqVO);

    /**
     * 获取否定品牌推荐
     */
    List<Brand> getNegativeBrandRecommendations(AmzAdvHelpNegativeBrandRecommendationReqVO reqVO);

    /**
     * 获取可投放类目
     */
    TargetableCategories getTargetableCategories(AmzAdvHelpTargetableCategoriesReqVO reqVO);

    /**
     * 获取产品推荐 (Suggested target ASINs)
     */
    Object getProductRecommendations(AmzAdvHelpProductRecommendationReqVO reqVO);

    /**
     * 获取产品元数据 (Product metadata)
     */
    Object getProductMetadata(AmzAdvHelpProductMetadataReqVO reqVO);
}
