package com.hzltd.module.amz.spapi;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.module.spapi.api.ServiceRegister;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.common.MediaModel;
import com.hzltd.module.spapi.model.product.*;
import com.hzltd.module.spapi.service.product.ProductApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.listings.items.v2021_08_01.ListingsApi;
import software.amazon.spapi.models.listings.items.v2021_08_01.Item;
import software.amazon.spapi.models.listings.items.v2021_08_01.ItemSearchResults;

import java.time.ZoneOffset;
import java.util.List;
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


    @Override
    public ApiResponse<MediaModel> uploadFile(ApiRequest<MediaModel> file) {
        return null;
    }

    @Override
    public ApiResponse<CreateProductResponse> createProduct(ApiRequest<CreateProductRequest> request) {
        return null;
    }

    @Override
    public ApiResponse<List<MultiMarketProductModel>> searchProduct(ApiRequest<SearchProductRequest> apiRequest) {
        ListingsApi listingsApi = getListingsApi(apiRequest);
        List<String> marketPlaceIds = getShopMarkets(apiRequest.getShopId());
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
            Item item = listingsApi.getListingsItem(sellerId, apiRequest.getRequest().getSellerSku(), this.getShopMarkets(apiRequest.getShopId()), null, ALL_CONTENT);
            return ApiResponse.success(convertProduct(item));
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

}
