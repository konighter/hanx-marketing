package com.hzltd.module.erplus.service.cross;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.service.PricingInventoryApiFactory;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossProductPriceUpdateRequest;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.cross.CrossProductMapper;
import com.hzltd.module.erplus.dal.mysql.cross.CrossProductPriceMapper;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.pricing.ChangePriceRequest;
import com.hzltd.module.erplus.spapi.model.pricing.ChangePriceResponse;
import com.hzltd.module.erplus.spapi.model.pricing.GetInventoryRequest;
import com.hzltd.module.erplus.spapi.model.pricing.GetOfferResponse;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
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
    private CrossProductMapper crossProductMapper;

    @Resource
    private CrossProductPriceMapper crossProductPriceMapper;

    @Lazy
    @Resource
    private ErplusCrossProductService crossProductService;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private PricingInventoryApiFactory pricingInventoryApiFactory;

    @Override
    public Boolean updateCrossPlatformProductPrice(CrossProductPriceUpdateRequest request) {
        Optional<CrossProductDO> crossProduct = crossProductService.getBasicCrossPlatformProduct(request.getProductId());
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
        Optional<CrossProductDO> crossProduct = crossProductService.getBasicCrossPlatformProduct(productId);
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
        Optional<CrossProductDO> crossProduct = crossProductService.getBasicCrossPlatformProduct(productId);
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
        Map<String, List<CrossProductDO>> productsByMarketId = productIds.stream()
                .map(crossProductService::getBasicCrossPlatformProduct)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(CrossProductDO::getMarketId));

    }

    @Override
    public CrossProductPriceDO getEffectivePrice(Long productId) {
        CrossProductDO product = crossProductMapper.selectById(productId);

        return crossProductPriceMapper.selectOne(new LambdaQueryWrapperX<CrossProductPriceDO>()
                .eqIfPresent(CrossProductPriceDO::getShopId, product.getShopId())
                .eqIfPresent(CrossProductPriceDO::getMarketId, product.getMarketId())
                .eqIfPresent(CrossProductPriceDO::getProductId, productId)
                .leIfPresent(CrossProductPriceDO::getStartAt, System.currentTimeMillis() / 1000)
                .and(wrapper -> {
                    wrapper.ge(CrossProductPriceDO::getEndAt, System.currentTimeMillis() / 1000)
                            .or()
                            .isNull(CrossProductPriceDO::getEndAt);
                })
        );



    }


}
