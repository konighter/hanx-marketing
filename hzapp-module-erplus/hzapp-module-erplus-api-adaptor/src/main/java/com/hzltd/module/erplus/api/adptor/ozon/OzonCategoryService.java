package com.hzltd.module.erplus.api.adptor.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.adptor.LocalAuthProvider;
import com.hzltd.module.erplus.api.adptor.ozon.proto.MetaMappingUtils;
import com.hzltd.module.erplus.api.adptor.ozon.proto.OzonApiResponse;
import com.hzltd.module.erplus.api.annotations.ServiceRegister;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.model.category.*;
import com.hzltd.module.erplus.service.category.CategoryApi;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.OZON, serviceClass = CategoryApi.class)
public class OzonCategoryService extends OzonPlatformApiService implements CategoryApi {
    @Override
    public ApiResponse<List<CategoryModel>> getCategories(ApiRequest<GetCategoryRequest> apiRequest) {
        apiRequest.setShopId(String.valueOf(apiRequest.getRequest().getShopId()));
        AuthorizationModel authModel = this.getAuthorizationModel(apiRequest);
        String resp = OzonHttpRequest.request("/v1/description-category/tree", Map.of("language", MetaMappingUtils.mapLanguage(apiRequest.getLanguage())), Map.of("Client-Id", authModel.getAppKey(), "Api-Key", authModel.getAppSecret()));
        OzonApiResponse<List<CategoryRespModel>> categoryRespModels = JsonUtils.parseObject(resp, new TypeReference<OzonApiResponse<List<CategoryRespModel>>>() {
        });

        return ApiResponse.success(MetaMappingUtils.mapCategoryList(categoryRespModels.getResult(), ""));
    }

    @Override
    public ApiResponse<List<CategoryAttributeModel>> getCategoryAttributes(ApiRequest<GetCategoryAttributeRequest> apiRequest) {
        AuthorizationModel authModel = this.getAuthorizationModel(apiRequest);
        String categoryId = apiRequest.getRequest().getCategoryIds().get(Math.max(0, apiRequest.getRequest().getCategoryIds().size() - 2));
        String typeId = apiRequest.getRequest().getCategoryId();
        String language = MetaMappingUtils.mapLanguage(apiRequest.getLanguage());
        String resp = OzonHttpRequest.request("/v1/description-category/attribute",
                Map.of("language", language,
                        "description_category_id", categoryId,
                        "type_id", typeId),
                Map.of("Client-Id", authModel.getAppKey(), "Api-Key", authModel.getAppSecret()));
        OzonApiResponse<List<CategoryAttributeRespModel>> categoryAttributeRespModels = JsonUtils.parseObject(resp, new TypeReference<OzonApiResponse<List<CategoryAttributeRespModel>>>() {
        });


        return ApiResponse.success(MetaMappingUtils.mapCategoryAttributeList(categoryAttributeRespModels.getResult(),
                categoryAttribute -> {
            return getCategoryAttributeValue(categoryAttribute.getId(), categoryId, typeId, language, authModel);
        }));
    }

    private List<CategoryAttributeValueRespModel> getCategoryAttributeValue(String attributeId, String categoryId, String typeId, String language, AuthorizationModel authModel) {
        String resp = OzonHttpRequest.request("/v1/description-category/attribute/values",
                Map.of("attribute_id", attributeId,
                        "language", language,
                        "description_category_id", categoryId,
                        "type_id", typeId,
                        "limit", 2000),
                Map.of("Client-Id", authModel.getAppKey(), "Api-Key", authModel.getAppSecret()));
        return JsonUtils.parseObject(resp, new TypeReference<OzonApiResponse<List<CategoryAttributeValueRespModel>>>() {}).getResult();
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

    @Data
    public static class CategoryRespModel {
        @JsonProperty("description_category_id")
        private String descriptionCategoryId;
        @JsonProperty("category_name")
        private String categoryName;
        @JsonProperty("disabled")
        private Boolean disabled;
        @JsonProperty("children")
        private List<CategoryRespModel> children;

        @JsonProperty("type_id")
        private String typeId;

        @JsonProperty("type_name")
        private String typeName;
    }

    /**
     * Represents a product characteristic or attribute definition in a product information system.
     */
    @Data
    public static class CategoryAttributeRespModel {

        // --- Core Attribute Identifiers and Basic Info ---

        /**
         * Characteristic identifier.
         * Corresponds to 'id' (integer <int64>).
         */
        private String id;

        /**
         * Name of the characteristic.
         * Corresponds to 'name' (string).
         */
        private String name;

        /**
         * Characteristic description.
         * Corresponds to 'description' (string).
         */
        private String description;

        /**
         * Characteristic type (e.g., 'string', 'integer', 'boolean').
         * Corresponds to 'type' (string).
         */
        private String type;

        // --- Grouping and Dictionary Info ---

        /**
         * Characteristics group identifier.
         * Corresponds to 'group_id' (integer <int64>).
         */
        @JsonProperty("group_id")
        private Long groupId;

        /**
         * Characteristics group name.
         * Corresponds to 'group_name' (string).
         */
        @JsonProperty("group_name")
        private String groupName;

        /**
         * Directory identifier (often for dictionary-type attributes).
         * Corresponds to 'dictionary_id' (integer <int64>).
         */
        @JsonProperty("dictionary_id")
        private Long dictionaryId;

        // --- Boolean Flags / Constraints ---

        /**
         * Indicates that the characteristic is mandatory:
         * true - a mandatory characteristic,
         * false - an optional characteristic.
         * Corresponds to 'is_required' (boolean).
         */
        @JsonProperty("is_required")
        private Boolean isRequired;

        /**
         * Indicates that the characteristic is a set of values (multi-value):
         * true - the characteristic is a set of values (a collection),
         * false - the characteristic consists of a single value.
         * Corresponds to 'is_collection' (boolean).
         */
        @JsonProperty("is_collection")
        private Boolean isCollection;

        /**
         * Indicates that the attribute is an aspect (distinguishes same-model products):
         * true - the attribute is aspect and can't be changed after delivery/sale.
         * false - the attribute is not aspect and can be changed at any time.
         * Corresponds to 'is_aspect' (boolean).
         */
        @JsonProperty("is_aspect")
        private Boolean isAspect;

        /**
         * Indication that the dictionary attribute values depend on the category:
         * true - the attribute has its own set of values for each category.
         * false - the attribute has the same set of values for all categories.
         * Corresponds to 'category_dependent' (boolean).
         */
        @JsonProperty("category_dependent")
        private Boolean categoryDependent;

        // --- Complex Attribute (Nested/Structured Attribute) Info ---

        /**
         * Complex attribute identifier (if this attribute is part of a complex structure).
         * Corresponds to 'attribute_complex_id' (integer <int64>).
         */
        @JsonProperty("attribute_complex_id")
        private Long attributeComplexId;

        /**
         * Maximum number of values allowed for the attribute.
         * Corresponds to 'max_value_count' (integer <int64>).
         */
        @JsonProperty("max_value_count")
        private Long maxValueCount;

        /**
         * Indicates if the complex characteristic is a set of values (multi-value complex attribute).
         * Corresponds to 'complex_is_collection' (boolean).
         */
        @JsonProperty("complex_is_collection")
        private Boolean complexIsCollection;

    }

    @Data
    public static class CategoryAttributeValueRespModel {
        private String id;
        private String value;

        private String info;

        private String picture;
    }
}
