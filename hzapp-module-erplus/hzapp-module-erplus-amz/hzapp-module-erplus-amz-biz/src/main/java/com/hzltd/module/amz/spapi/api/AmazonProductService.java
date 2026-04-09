package com.hzltd.module.amz.spapi.api;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.amz.spapi.utils.AmazonListingParserUtils;
import com.hzltd.module.erplus.spapi.api.ServiceRegister;
import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.common.FeeModel;
import com.hzltd.module.erplus.spapi.model.common.MediaModel;
import com.hzltd.module.erplus.spapi.model.product.*;
import com.hzltd.module.erplus.spapi.service.product.ProductApi;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.listings.items.v2021_08_01.ListingsApi;
import software.amazon.spapi.api.productfees.v0.FeesApi;
import software.amazon.spapi.models.listings.items.v2021_08_01.Item;
import software.amazon.spapi.models.listings.items.v2021_08_01.ItemSearchResults;
import software.amazon.spapi.models.listings.items.v2021_08_01.ListingsItemPutRequest;
import software.amazon.spapi.models.listings.items.v2021_08_01.ListingsItemSubmissionResponse;
import software.amazon.spapi.models.productfees.v0.*;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = ProductApi.class)
public class AmazonProductService extends AbsAmzPlatformApiService implements ProductApi {

    private static final List<String> ALL_CONTENT = List.of(
            "summaries",
            "attributes",
            "issues",
            "offers",
            "fulfillmentAvailability",
            "procurement",
            "relationships",
            "productTypes"
    );

    private static final List<String> SUMMARY_CONTENT = List.of("summaries", "attributes", "issues");

    @Resource
    private SystemShopService shopService;


    @Override
    public ApiResponse<MediaModel> uploadFile(ApiRequest<MediaModel> file) {
        return null;
    }

    @Override
    public ApiResponse<CreateProductResponse> createProduct(ApiRequest<CreateProductRequest> request) {
        ListingsApi listingsApi = getListingsApi(request);

        ShopModel shopModel = shopService.getShopById(Long.valueOf(request.getShopId()));
        CreateProductRequest createProductRequest = request.getRequest();

        try {
            ListingsItemPutRequest listingsItemPutRequest = new ListingsItemPutRequest().attributes(createProductRequest.getCrossPlatformExtAttrs()).productType(createProductRequest.getCategory().getCategoryId());
            // item_name, item_type_keyword, brand
            listingsItemPutRequest.putAttributesItem("item_name", List.of(Map.of("value", createProductRequest.getTitle())));
            listingsItemPutRequest.putAttributesItem("item_type_keyword", List.of(Map.of("value", createProductRequest.getCategory().getCategoryId())));
            listingsItemPutRequest.putAttributesItem("brand", List.of(Map.of("value", "NewOasis")));


            ListingsItemSubmissionResponse listingSubmitResp = listingsApi.putListingsItem(listingsItemPutRequest, shopModel.getSellerId(), createProductRequest.getSkus().get(0).getSellerSku(), List.of(shopModel.getMarketplace()), null, "VALIDATION_PREVIEW", "en_US");
            return ApiResponse.success(new CreateProductResponse());

        } catch (ApiException | LWAException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<MultiMarketProductModel>> searchProduct(ApiRequest<SearchProductRequest> apiRequest) {
        ListingsApi listingsApi = getListingsApi(apiRequest);
        List<String> marketPlaceIds = CollectionUtils.isNotEmpty(apiRequest.getRequest().getMarketplaceIds()) ? apiRequest.getRequest().getMarketplaceIds() : List.of(apiRequest.getMarketId());
        String sellerId = this.getAuthorizationModel(apiRequest).getSellerId();
        SearchProductRequest query = apiRequest.getRequest();
        try {
            OffsetDateTime createTimeStart = query.getCreateTimeStart() == null ? null : convert(java.time.OffsetDateTime.of(query.getCreateTimeStart().toLocalDateTime(), ZoneOffset.UTC));
            OffsetDateTime createTimeEnd = query.getCreateTimeEnd() == null ? null : convert(java.time.OffsetDateTime.of(query.getCreateTimeEnd().toLocalDateTime(), ZoneOffset.UTC));
            OffsetDateTime updateTimeStart = query.getUpdateTimeStart() == null ? null : convert(java.time.OffsetDateTime.of(query.getUpdateTimeStart().toLocalDateTime(), ZoneOffset.UTC));
            OffsetDateTime updateTimeEnd = query.getUpdateTimeEnd() == null ? null : convert(java.time.OffsetDateTime.of(query.getUpdateTimeEnd().toLocalDateTime(), ZoneOffset.UTC));

            String identifierCodeType = null;
            List<String> identifierCodes = null;
            if (CollectionUtils.isNotEmpty(query.getSellerSkus())) {
                identifierCodeType = "SKU";
                identifierCodes = query.getSellerSkus();
            } else if (CollectionUtils.isNotEmpty(query.getProductCodes())) {
                identifierCodeType = "ASIN";
                identifierCodes = query.getProductCodes();
            }

            ItemSearchResults itemSearchResults = listingsApi.searchListingsItems(sellerId, marketPlaceIds, apiRequest.getLocale().toString(), query.isIfAllContent() ? ALL_CONTENT : SUMMARY_CONTENT, identifierCodes, identifierCodeType, null, null, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd, null, null, null, "lastUpdatedDate", "DESC", query.getPageSize(), query.getCursor());
            return ApiResponse.success(itemSearchResults.getItems().stream().map(this::convertProduct).collect(Collectors.toList()))
                    .setTotal(itemSearchResults.getNumberOfResults())
                    .setCursor(itemSearchResults.getPagination() != null ? itemSearchResults.getPagination().getNextToken() : "")
                    .setPage(new PageParam().setPageNo(query.getPageNo()).setPageSize(query.getPageSize()));
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponse<MultiMarketProductModel> getProduct(ApiRequest<GetProductRequest> apiRequest) {
        ListingsApi listingsApi = getListingsApi(apiRequest);
        String sellerId = this.getAuthorizationModel(apiRequest).getSellerId();
        try {
            Item item = listingsApi.getListingsItem(sellerId, apiRequest.getRequest().getSellerSku(), List.of(apiRequest.getMarketId()), null, ALL_CONTENT);
            return ApiResponse.success(convertProduct(item));
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponse<FeeModel> getProductFee(ApiRequest<ProductFeeRequest> request) {

        FeesApi productFeeApi = getProductFeeApi(request);

        ProductFeeRequest feeRequest = request.getRequest();
        try {
            GetMyFeesEstimateResponse response = productFeeApi.getMyFeesEstimateForASIN(new GetMyFeesEstimateRequest()
                    .feesEstimateRequest(new FeesEstimateRequest()
                            .marketplaceId(request.getMarketId())
                                    .isAmazonFulfilled(FulfillTypeEnum.FBA.equals(feeRequest.getFulfillType()))
                            .priceToEstimateFees(new PriceToEstimateFees().listingPrice(new MoneyType().amount(feeRequest.getPrice().getAmount())))
                                    .identifier(UUID.randomUUID().toString())
                            ), feeRequest.getPlatformProductCode());
            if (response.getPayload().getFeesEstimateResult().getFeesEstimate() != null) {
                FeesEstimate feesEstimate = response.getPayload().getFeesEstimateResult().getFeesEstimate();
                FeeModel feeModel = new FeeModel();
                feeModel.setTotalFee(feesEstimate.getTotalFeesEstimate().getAmount().multiply(BigDecimal.valueOf(100)).intValue());
                feeModel.setCurrency(feesEstimate.getTotalFeesEstimate().getCurrencyCode());
                feeModel.setFeeItems(feesEstimate.getFeeDetailList().stream().map(detail -> {
                    FeeModel.FeeItem item = new FeeModel.FeeItem();
                    item.setFeeType(detail.getFeeType())
                            .setFeeAmount(detail.getFeeAmount().getAmount().multiply(BigDecimal.valueOf(100)).intValue())
                            .setCurrency(detail.getFeeAmount().getCurrencyCode())
                            .setFinalFee(detail.getFinalFee().getAmount().multiply(BigDecimal.valueOf(100)).intValue());
                    return item;
                }).toList());

            } else {
                return ApiResponse.error("No ProductFee Estimate Info");
            }
        } catch (ApiException|LWAException e) {
            log.error("getProductFee Error", e);
            return ApiResponse.error(e.getMessage());
        }


        return ProductApi.super.getProductFee(request);
    }

    @Override
    public MultiMarketProductModel convertProduct(Item item) {
        try {
            return AmazonListingParserUtils.parse(JsonUtils.parseTree(JsonUtils.toJsonString(item)));
        } catch (Exception e) {
            log.error("Failed to parse Amazon Listing Item: sku={}", item.getSku(), e);
            // Fallback to parent implementation if parsing fails
            return super.convertProduct(item);
        }
    }
}
