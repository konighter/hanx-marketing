package com.hzltd.module.erplus.service.ecom;

import com.hzltd.module.erplus.constant.AttributeTypeEnum;
import com.hzltd.module.erplus.controller.admin.category.vo.ProductCategoryListReqVO;
import com.hzltd.module.erplus.dal.dataobject.categoryattr.CategoryAttributeDO;
import com.hzltd.module.erplus.dal.dataobject.product.ProductCategoryDO;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.category.*;
import com.hzltd.module.erplus.model.common.MediaModel;
import com.hzltd.module.erplus.model.product.CreateProductRequest;
import com.hzltd.module.erplus.model.product.CreateProductResponse;
import com.hzltd.module.erplus.service.PlatformIdentity;
import com.hzltd.module.erplus.service.category.CategoryApi;
import com.hzltd.module.erplus.service.categoryattr.CategoryAttributeService;
import com.hzltd.module.erplus.service.product.ProductCategoryService;
import com.hzltd.module.erplus.service.product.ProductApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultHanXCrossServiceApi implements ProductApi, CategoryApi, PlatformIdentity {

    @Resource
    private CategoryAttributeService categoryAttributeService;

    @Resource
    private ProductCategoryService categoryService;


    @Override
    public ApiResponse<List<CategoryModel>> getCategories(ApiRequest<GetCategoryRequest> apiRequest) {
        List<ProductCategoryDO> categories = categoryService.getProductCategoryList(new ProductCategoryListReqVO());
       return ApiResponse.success(categories.stream().map(c -> {
            CategoryModel model = new CategoryModel();
            model.setCategoryId(c.getId().toString());
            model.setName(c.getName());
            model.setParentCategoryId(c.getParentId().toString());
            return model;
        }).collect(Collectors.toList()));

    }

    @Override
    public ApiResponse<List<CategoryAttributeModel>> getCategoryAttributes(ApiRequest<GetCategoryAttributeRequest> apiRequest) {
        CategoryAttributeDO attributeDO = new CategoryAttributeDO();
        attributeDO.setCategoryId(apiRequest.getRequest().getCategoryId());
        List<CategoryAttributeDO> categoryAttributes = categoryAttributeService.getCategoryAttributes(attributeDO);
        return ApiResponse.success( categoryAttributes.stream().map(a -> {
            CategoryAttributeModel model = new CategoryAttributeModel();
            model.setAttrId(a.getAttrId());
            model.setAttrName(a.getAttrName());
            model.setAttrType(AttributeTypeEnum.valueOf(a.getAttrType()));
            model.setRequired(a.isRequired());
            return model;
        }).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<List<CategoryModel>> getGlobalCategories(ApiRequest<GetCategoryRequest> apiRequest) {
        return null;
    }

    @Override
    public ApiResponse<CategoryRuleModel> getCategoryRules(ApiRequest<String> categoryId) {
        return null;
    }

    @Override
    public ApiResponse<List<CategoryModel>> getRecommendCategories(ApiRequest<GetRecommendCategoryRequest> apiRequest) {
        return null;
    }

    @Override
    public ApiResponse<List<BrandModel>> getBrands(ApiRequest<GetBrandRequest> apiRequest) {
        return null;
    }

    @Override
    public ApiResponse<BrandModel> createCustomBrand(ApiRequest<String> brandName) {
        return null;
    }

    @Override
    public ApiResponse<MediaModel> uploadFile(ApiRequest<MediaModel> file) {
        return null;
    }

    @Override
    public ApiResponse<CreateProductResponse> createProduct(ApiRequest<CreateProductRequest> request) {
        return null;
    }

    @Override
    public CrossPlatformEnum getPlatform() {
        return CrossPlatformEnum.LOCAL;
    }
}
