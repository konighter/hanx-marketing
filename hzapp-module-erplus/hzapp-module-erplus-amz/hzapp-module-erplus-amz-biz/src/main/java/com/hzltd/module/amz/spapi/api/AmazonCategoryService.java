package com.hzltd.module.amz.spapi.api;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.amz.spapi.proto.ProductTypeSchemaUtils;
import com.hzltd.module.erplus.spapi.api.ServiceRegister;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.category.*;
import com.hzltd.module.erplus.spapi.service.category.CategoryApi;
import com.hzltd.module.erplus.system.annotation.CrossplatformApiLog;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.service.SystemShopService;
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
    @CrossplatformApiLog
    public ApiResponse<List<CategoryModel>> getCategories(ApiRequest<GetCategoryRequest> apiRequest) {
        try {
            apiRequest.setShopId(String.valueOf(apiRequest.getRequest().getShopId()));
            DefinitionsApi definitionsApi = getDefinitionsApi(apiRequest);

            ProductTypeList productTypes = definitionsApi.searchDefinitionsProductTypes(systemShopService.getShopMarketplace(apiRequest.getShopId()), List.of(apiRequest.getRequest().getName()), null, "en_US", null);
            return ApiResponse.success(productTypes.getProductTypes().stream()
                    .map(productType -> {
                        CategoryModel model = new CategoryModel();
                        model.setCategoryId(productType.getName());
                        model.setName(productType.getDisplayName());
                        model.setLeaf(true);
                        return model;
                    }).collect(Collectors.toList())
                    );
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @CrossplatformApiLog
    public ApiResponse<MetaCategorySchemaResult> getCategoryAttributes(ApiRequest<GetCategoryAttributeRequest> apiRequest) {

        DefinitionsApi definitionsApi = getDefinitionsApi(apiRequest);
        try {
            ProductTypeDefinition productTypeDefinition = definitionsApi.getDefinitionsProductType(
                apiRequest.getRequest().getCategoryId(), 
                systemShopService.getShopMarketplace(apiRequest.getShopId()), 
                null, null, null, null, "en_US"
            );
            MetaCategorySchemaResult result = ProductTypeSchemaUtils.parseProductTypeSchema(productTypeDefinition.getSchema().getLink().getResource(), productTypeDefinition);
            return ApiResponse.success(result);
        } catch (ApiException | LWAException e) {
            log.error("[getCategoryAttributes] Failed for category {}", apiRequest.getRequest().getCategoryId(), e);
            throw new RuntimeException(e);
        }

    }
    
    @Override
    public ApiResponse<String> getCategorySchema(ApiRequest<GetCategoryAttributeRequest> apiRequest) {
        DefinitionsApi definitionsApi = getDefinitionsApi(apiRequest);
        try {
            ProductTypeDefinition productTypeDefinition = definitionsApi.getDefinitionsProductType(
                apiRequest.getRequest().getCategoryId(),
                systemShopService.getShopMarketplace(apiRequest.getShopId()),
                    null, null, null, null, "en_US"
            );
            String rawSchema = ProductTypeSchemaUtils.getProductTypeSchema(productTypeDefinition.getSchema().getLink().getResource());
            return ApiResponse.success(rawSchema);
        } catch (ApiException | LWAException e) {
            log.error("[getCategorySchema] Failed to fetch schema for {}", apiRequest.getRequest().getCategoryId(), e);
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
