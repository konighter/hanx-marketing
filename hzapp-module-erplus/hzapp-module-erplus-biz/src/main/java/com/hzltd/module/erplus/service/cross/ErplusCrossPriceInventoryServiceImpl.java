package com.hzltd.module.erplus.service.product;

import com.hzltd.module.erplus.api.service.PricingInventoryApiFactory;
import com.hzltd.module.erplus.controller.admin.product.vo.CrossProductPriceUpdateRequest;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.product.ErpCrossProductMapper;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.pricing.ChangePriceRequest;
import com.hzltd.module.erplus.model.pricing.ChangePriceResponse;
import com.hzltd.module.erplus.model.pricing.GetInventoryRequest;
import com.hzltd.module.erplus.model.pricing.GetOfferResponse;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ErplusCrossPriceInventoryServiceImpl implements ErplusCrossPriceInventoryService {

    @Resource
    private ErpCrossProductMapper crossProductMapper;

    @Lazy
    @Resource
    private ErplusCrossProductService crossProductService;

    @Resource
    private SellPlatformService sellPlatformService;


    @Resource
    private PricingInventoryApiFactory pricingInventoryApiFactory;

    @Override
    public Boolean updateCrossPlatformProductPrice(CrossProductPriceUpdateRequest request) {
        Optional<ErpCrossProductDO> crossProduct = crossProductService.getBasicCrossPlatformProduct(request.getProductId());
        if (crossProduct.isEmpty()) {
            log.warn("getCrossPlatformProductPrice, crossProduct not found, productId: {}", request.getProductId());
            return false;
        }

        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(crossProduct.get().getPlatformId());

        ApiRequest<ChangePriceRequest> changeProductPricingRequestApiRequest = new ApiRequest<ChangePriceRequest>()
                .setCrossPlatform(CrossPlatformEnum.of(sellPlatform.getCode()))
                .setShopId(crossProduct.get().getShopId().toString())
                .setMarketId(crossProduct.get().getMarketId())
                .setRequest(new ChangePriceRequest()
                        .setProductCode(crossProduct.get().getPlatformProductCode())
                        .setSellerSku(crossProduct.get().getSellerSkuCode())
                        .setCategory(crossProduct.get().getCategoryId())
                        .setListingPrice(request.getListingPrice())
                        .setSalePrice(request.getSalePrice()));

        ApiResponse<ChangePriceResponse> resp = pricingInventoryApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                .changeProductPricing(changeProductPricingRequestApiRequest);


        return resp.success();
    }

     @Override
    public GetOfferResponse getCrossProductOffer(Long productId) {
        Optional<ErpCrossProductDO> crossProduct = crossProductService.getBasicCrossPlatformProduct(productId);
        if (crossProduct.isEmpty()) {
            log.warn("getCrossPlatformProductPrice, crossProduct not found, productId: {}", productId);
            return null;
        }
        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(crossProduct.get().getPlatformId());

         ApiResponse<GetOfferResponse> resp  = pricingInventoryApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                 .getItemOffers(new ApiRequest<String>()
                         .setCrossPlatform(CrossPlatformEnum.of(sellPlatform.getCode()))
                         .setShopId(crossProduct.get().getShopId().toString())
                         .setMarketId(crossProduct.get().getMarketId())
                         .setRequest(crossProduct.get().getSellerSkuCode())
                 );

         log.info("getCrossPlatformProductPrice, resp: {}", resp);

        return resp.getData();
    }

    @Override
    public GetOfferResponse getCrossProductAllOffer(String productCode, CrossPlatformEnum platform, Integer shopId, String marketId) {
        ApiResponse<GetOfferResponse> resp  = pricingInventoryApiFactory.getCrossApiService(platform)
                .getItemOffers(new ApiRequest<String>()
                        .setCrossPlatform(platform)
                        .setShopId(shopId.toString())
                        .setMarketId(marketId)
                        .setRequest(productCode)
                );

        log.info("getCrossPlatformProductAllOffer, resp: {}", resp);
        return resp.getData();
    }


    @Override
    public void getCrossInventory(Long productId) {
        Optional<ErpCrossProductDO> crossProduct = crossProductService.getBasicCrossPlatformProduct(productId);
        if (crossProduct.isEmpty()) {
            log.warn("getCrossPlatformProductPrice, crossProduct not found, productId: {}", productId);
            return;
        }
        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(crossProduct.get().getPlatformId());

        ApiRequest<GetInventoryRequest> request = new ApiRequest<GetInventoryRequest>()
                .setCrossPlatform(CrossPlatformEnum.of(sellPlatform.getCode()))
                .setShopIdInt(crossProduct.get().getShopId())
                .setMarketId(crossProduct.get().getMarketId())
                .setRequest(new GetInventoryRequest().setSellerSkus(List.of(crossProduct.get().getSellerSkuCode())));

        pricingInventoryApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                .getInventory(request);

    }

    @Override
    public void getCrossInventories(List<Long> productIds) {
        // 按MarketId分组
        Map<String, List<ErpCrossProductDO>> productsByMarketId = productIds.stream()
                .map(crossProductService::getBasicCrossPlatformProduct)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(ErpCrossProductDO::getMarketId));



    }
}
