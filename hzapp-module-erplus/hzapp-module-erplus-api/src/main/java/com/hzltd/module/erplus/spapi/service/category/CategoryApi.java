package com.hzltd.module.erplus.spapi.service.category;

import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.category.*;

import java.util.List;

public interface CategoryApi {
    /**
     * 获取商品品类, 与店铺、区域、关键词相关
     * @param apiRequest
     * @return
     */
    ApiResponse<List<CategoryModel>> getCategories(ApiRequest<GetCategoryRequest> apiRequest);

    /**
     * 获取品类属性 (含完整 JSON Schema)
     * @param apiRequest
     * @return
     */
    ApiResponse<MetaCategorySchemaResult> getCategoryAttributes(ApiRequest<GetCategoryAttributeRequest> apiRequest);

    /**
     * 获取全局属性
     * @param apiRequest
     * @return
     */
    ApiResponse<List<CategoryModel>> getGlobalCategories(ApiRequest<GetCategoryRequest> apiRequest);

    /**
     * 获取品类限制
     * @param categoryId
     * @return
     */
    ApiResponse<CategoryRuleModel> getCategoryRules(ApiRequest<String> categoryId);


    /**
     * 获取推荐的品类
     * @return
     */
    ApiResponse<List<CategoryModel>> getRecommendCategories(ApiRequest<GetRecommendCategoryRequest> apiRequest);

    /**
     * 获取品牌
     * @param apiRequest
     * @return
     */
    ApiResponse<List<BrandModel>> getBrands(ApiRequest<GetBrandRequest> apiRequest);

    /**
     * 创建自定义品牌
     * @param brandName
     * @return
     */
    ApiResponse<BrandModel> createCustomBrand(ApiRequest<String> brandName);

    /**
     * 获取品类完整的 JSON Schema (Raw)
     * @param apiRequest
     * @return
     */
    ApiResponse<String> getCategorySchema(ApiRequest<GetCategoryAttributeRequest> apiRequest);
}
