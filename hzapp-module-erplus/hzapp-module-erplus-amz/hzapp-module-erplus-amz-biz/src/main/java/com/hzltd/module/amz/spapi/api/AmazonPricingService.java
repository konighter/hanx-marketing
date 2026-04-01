package com.hzltd.module.amz.spapi.api;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.amz.spapi.model.AmzPurchasableOfferModel;
import com.hzltd.module.spapi.api.ServiceRegister;
import com.hzltd.module.spapi.enums.CrossOperationStatus;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.spapi.model.common.InventoryModel;
import com.hzltd.module.spapi.model.pricing.*;
import com.hzltd.module.spapi.service.product.PricingInventoryApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.fba.inventory.v1.FbaInventoryApi;
import software.amazon.spapi.api.listings.items.v2021_08_01.ListingsApi;
import software.amazon.spapi.api.pricing.v0.ProductPricingApi;
import software.amazon.spapi.models.fba.inventory.v1.GetInventorySummariesResponse;
import software.amazon.spapi.models.fba.inventory.v1.InventoryDetails;
import software.amazon.spapi.models.fba.inventory.v1.InventorySummary;
import software.amazon.spapi.models.listings.items.v2021_08_01.ListingsItemPatchRequest;
import software.amazon.spapi.models.listings.items.v2021_08_01.ListingsItemSubmissionResponse;
import software.amazon.spapi.models.listings.items.v2021_08_01.PatchOperation;
import software.amazon.spapi.models.pricing.v0.CustomerType;
import software.amazon.spapi.models.pricing.v0.GetOffersResponse;
import software.amazon.spapi.models.pricing.v0.ItemCondition;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = PricingInventoryApi.class)
public class AmazonPricingService extends AbsAmzPlatformApiService implements PricingInventoryApi {

    /**
     * 获取ASIN的竞争力价格
     * @param request
     * @return
     */
    @Override
    public ApiResponse<GetOfferResponse> getCompetitivePricing(ApiRequest<?> request) {
        ProductPricingApi productPricingApi = this.getPricingApi(request);
        return null;
//        try {
//            productPricingApi.getCompetitivePricing(request.getMarketId(), ItemCondition.NEW.getValue(),request.getRequest(), CustomerType.CONSUMER.getValue());
//            return ApiResponse.success(null);
//        } catch (ApiException | LWAException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * 获取ASIN的价格, 返回20条
     * @param request
     * @return
     */
    @Override
    public ApiResponse<GetOfferResponse> getItemOffers(ApiRequest<String> request) {
        ProductPricingApi productPricingApi = this.getPricingApi(request);
        try {
            GetOffersResponse response = productPricingApi.getItemOffers(request.getMarketId(), ItemCondition.NEW.getValue(), request.getRequest(), CustomerType.CONSUMER.getValue());
            return ApiResponse.success(convert(response));
        } catch (ApiException | LWAException e) {
            log.error("getItemOffers error, request: {}, {}", request, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取ASIN的报价
     * 关键词：针对自己的 SKU、竞争环境
     *
     * 这个接口是基于卖家自己的库存 (Listing) 来查询竞争对手的优惠信息。
     *
     * 查询参数：必须传入 SellerSKU。
     *
     * 权限限制：你必须已经刊登了该 SKU。如果你没有在卖这个 SKU，调用会报错。
     *
     * 返回内容：返回该 SKU 对应 ASIN 下前 20 个最低价优惠。包含配送方式（FBA/FBM）、卖家评价等级、是否是购物车等。
     *
     * 适用场景：针对你正在销售的商品进行调价决策。它可以让你看到你的 SKU 在当前市场中的排名和具体的竞争对手分布。
     * @param request
     * @return
     */
    @Override
    public ApiResponse<GetOfferResponse> getListingsOffers(ApiRequest<String> request) {

        ProductPricingApi productPricingApi = this.getPricingApi(request);
        try {
            GetOffersResponse response = productPricingApi.getListingOffers(request.getMarketId(), ItemCondition.NEW.getValue(), request.getRequest(), CustomerType.CONSUMER.getValue());
            return ApiResponse.success(convert(response));
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponse<ChangePriceResponse> changeProductPricing(ApiRequest<ChangePriceRequest> request) {
        ListingsApi listingsApi = this.getListingsApi(request);
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
            try {
                ListingsItemPatchRequest patchRequest = convertPricingRequest(request);
                ListingsItemSubmissionResponse response = listingsApi.patchListingsItem(patchRequest, authorizationModel.getSellerId(), request.getRequest().getSellerSku(), List.of(request.getMarketId()), List.of("issues"), request.getRequest().isPreview() ? "VALIDATION_PREVIEW" : null, request.getLocale().toString());
                ChangePriceResponse apiResp = new ChangePriceResponse().setStatus(CrossOperationStatus.of(response.getStatus().getValue()));
                if (CollectionUtils.isNotEmpty(response.getIssues())) {
                    apiResp.setIssues(response.getIssues().stream().map(this::convert).collect(Collectors.toList()));
                }
                return ApiResponse.success(apiResp);
            } catch (ApiException | LWAException e) {
                throw new RuntimeException(e);
            }
    }

    private ListingsItemPatchRequest convertPricingRequest(ApiRequest<ChangePriceRequest> request) {
        ChangePriceRequest changeProductPriceRequest = request.getRequest();

        return new ListingsItemPatchRequest()
                .productType(changeProductPriceRequest.getCategory())
                .addPatchesItem(new PatchOperation()
                        .op(PatchOperation.OpEnum.REPLACE)
                        .path(AmzPurchasableOfferModel.ATTR_PATH)
                        .value(changeProductPriceRequest.getSalePrice().stream().map(
                                price -> {
                                    return new AmzPurchasableOfferModel()
                                            .addPrice(price.getAmount().doubleValue())
                                            .setCurrency(price.getCurrency())
                                            .setAudience(null)
                                            .setMarketplaceId(request.getMarketId())
                                            .setCurrency(price.getCurrency())
                                            .setStartAt(formatToUTC(price.getStartAt()))
                                            .setEndAt(formatToUTC(price.getEndAt()));
                                }
                        ).collect(Collectors.toList())));

    }

    @Override
    public ApiResponse<ChangeInventoryResponse> changeProductInventory(ApiRequest<ChangeInventoryRequest> request) {
        return ApiResponse.success(null);
    }

    @Override
    public ApiResponse<List<InventoryModel>> getInventory(ApiRequest<GetInventoryRequest> request) {
        FbaInventoryApi fbaInventoryApi = this.getFbaInventoryApi(request);
        try {
            GetInventorySummariesResponse response = fbaInventoryApi.getInventorySummaries("Marketplace", request.getMarketId(), List.of(request.getMarketId()), true, null, request.getRequest().getSellerSkus(), null, request.getRequest().getNextToken());
            List<InventoryModel> inventoryModels = response.getPayload().getInventorySummaries().stream().map(this::convert).collect(Collectors.toList());
            ApiResponse<List<InventoryModel>> apiResponse = ApiResponse.success(inventoryModels);
            if (response.getPagination() != null) {
                apiResponse.setCursor(response.getPagination().getNextToken());
            }
            return apiResponse;

        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    private InventoryModel convert(InventorySummary inventorySummary) {

        if (inventorySummary == null) {
            return null;
        }

        InventoryModel inventoryModel = new InventoryModel()
                .setPlatformProductCode(inventorySummary.getSellerSku())
                .setSellerSku(inventorySummary.getSellerSku())
                .setFnSku(inventorySummary.getFnSku())
                .setLastUpdateTime(inventorySummary.getLastUpdatedTime().toEpochSecond());

        if (inventorySummary.getInventoryDetails() != null) {
            InventoryDetails inventoryDetails = inventorySummary.getInventoryDetails();

            inventoryModel.setFulfillableQuantity(inventoryDetails.getFulfillableQuantity())
                    .setInboundWorkingQuantity(inventoryDetails.getInboundWorkingQuantity())
                    .setInboundShippedQuantity(inventoryDetails.getInboundShippedQuantity())
                    .setInboundReceivingQuantity(inventoryDetails.getInboundReceivingQuantity());

            if (inventoryDetails.getReservedQuantity() != null) {
                inventoryModel.setReservedQuantity(inventoryDetails.getReservedQuantity().getTotalReservedQuantity())
                        .setReservedPendingCustomerOrderQuantity(inventoryDetails.getReservedQuantity().getPendingCustomerOrderQuantity())
                        .setReservedPendingTransshipmentQuantity(inventoryDetails.getReservedQuantity().getPendingTransshipmentQuantity())
                        .setReservedFcProcessingQuantity(inventoryDetails.getReservedQuantity().getFcProcessingQuantity());
            }

            if (inventoryDetails.getResearchingQuantity() != null) {
                inventoryModel.setResearchingQuantity(inventoryDetails.getResearchingQuantity().getTotalResearchingQuantity());
            }

             if (inventoryDetails.getUnfulfillableQuantity() != null) {
                inventoryModel.setUnfulfillableQuantity(inventoryDetails.getUnfulfillableQuantity().getTotalUnfulfillableQuantity());
            }

        }

        return inventoryModel;
    }

}
