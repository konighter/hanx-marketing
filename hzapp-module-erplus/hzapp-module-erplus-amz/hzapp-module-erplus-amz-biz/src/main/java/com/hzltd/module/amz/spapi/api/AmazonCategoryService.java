package com.hzltd.module.amz.spapi.api;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.amz.spapi.proto.ProductTypeSchemaUtils;
import com.hzltd.module.spapi.api.ServiceRegister;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.category.*;
import com.hzltd.module.spapi.service.category.CategoryApi;
import com.hzltd.module.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.producttypedefinitions.v2020_09_01.DefinitionsApi;
import software.amazon.spapi.models.producttypedefinitions.v2020_09_01.ProductTypeDefinition;
import software.amazon.spapi.models.producttypedefinitions.v2020_09_01.ProductTypeList;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = CategoryApi.class)
public class AmazonCategoryService extends AbsAmzPlatformApiService implements CategoryApi {

    @Resource
    private SystemShopService systemShopService;


    @Override
    public ApiResponse<List<CategoryModel>> getCategories(ApiRequest<GetCategoryRequest> apiRequest) {
        try {
            apiRequest.setShopId(String.valueOf(apiRequest.getRequest().getShopId()));
            DefinitionsApi definitionsApi = getDefinitionsApi(apiRequest);

            ProductTypeList productTypes = definitionsApi.searchDefinitionsProductTypes(systemShopService.getShopRegion(apiRequest.getShopId()), List.of(apiRequest.getRequest().getName()), null, "zh_CN", null);
            return ApiResponse.success(productTypes.getProductTypes().stream()
                    .map(productType -> {
                        CategoryModel model = new CategoryModel();
                        model.setCategoryId(productType.getName());
                        model.setName(productType.getDisplayName());
                        model.setLeaf(true);
                        return model;
                    }).collect(Collectors.toList())
                    );
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (LWAException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ApiResponse<List<CategoryAttributeModel>> getCategoryAttributes(ApiRequest<GetCategoryAttributeRequest> apiRequest) {

        DefinitionsApi definitionsApi = getDefinitionsApi(apiRequest);
        try {
            ProductTypeDefinition productTypeDefinition = definitionsApi.getDefinitionsProductType(apiRequest.getRequest().getCategoryId(), systemShopService.getShopRegion(apiRequest.getShopId()), null, null, null, null, "zh_CN");
            List<CategoryAttributeModel> categoryAttributeModels = ProductTypeSchemaUtils.parseProductTypeSchema(productTypeDefinition.getSchema().getLink().getResource(), productTypeDefinition);
            return ApiResponse.success(categoryAttributeModels);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (LWAException e) {
            throw new RuntimeException(e);
        }

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

}
