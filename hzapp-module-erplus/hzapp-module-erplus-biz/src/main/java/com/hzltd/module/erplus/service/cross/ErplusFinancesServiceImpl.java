package com.hzltd.module.erplus.service.cross;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.service.FinancesApiFactory;
import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderResp;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.cross.CrossProductMapper;
import com.hzltd.module.spapi.enums.CrossOrderStatus;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.common.FeeModel;
import com.hzltd.module.spapi.model.order.ProductFeeEstimateRequest;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErplusFinancesServiceImpl implements ErplusFinancesService {

    @Lazy
    @Resource
    private ErplusCrossOrderService crossOrderService;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private ErplusCrossPriceInventoryService crossPriceInventoryService;

    @Resource
    private CrossProductMapper crossProductMapper;


    @Resource
    private FinancesApiFactory financesApiFactory;


    @Override
    public void syncProductFee(List<Long> orderId) {
        List<CrossOrderResp> crossOrderList = crossOrderService.getCrossOrders(orderId);

        Multimap<Integer, CrossOrderResp> crossOrderMap = ArrayListMultimap.create();
        crossOrderList.forEach(order -> crossOrderMap.put(order.getPlatformId(), order));
        crossOrderMap.asMap().forEach(this::syncOrderFeeByPlatform);
    }


    private void syncOrderFeeByPlatform(Integer platformId, Collection<CrossOrderResp> orderList) {
        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(platformId);
        // 暂时只处理亚马逊
        if (!CrossPlatformEnum.AMAZON.getCode().equals(sellPlatform.getCode())) {
            return;
        }

        orderList.forEach(order -> {
            // 按照实际处理
            if (CrossOrderStatus.SHIPPED.getStatus().equals(order.getStatus())) {
                return;
            } else {
                // 按照预估处理

            }

        });
    }

    public List<FeeModel> getProductEstimatedFee(Integer platformId, Integer shopId, String marketId, List<String> sellerSkuList) {
        List<CrossProductDO> crossProducts = crossProductMapper.selectList(new LambdaQueryWrapperX<CrossProductDO>()
                .eqIfPresent(CrossProductDO::getPlatformId, platformId)
                .eqIfPresent(CrossProductDO::getShopId, shopId)
                .eqIfPresent(CrossProductDO::getMarketId, marketId)
                .inIfPresent(CrossProductDO::getSellerSkuCode, sellerSkuList)
        );

        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(platformId);
        List<ProductFeeEstimateRequest> feeEstimateRequests = crossProducts.stream()
                .map(crossProduct -> {
                            ProductFeeEstimateRequest feeRequest = new ProductFeeEstimateRequest()
                                    .setSellerSku(crossProduct.getSellerSkuCode())
                                    .setFulfillType(FulfillTypeEnum.of(crossProduct.getFulfillType()))
                                    .setPlatformProductCode(crossProduct.getPlatformProductCode());
                            CrossProductPriceDO crossProductPrice = crossPriceInventoryService.getEffectivePrice(crossProduct.getId());
                            if (crossProductPrice != null) {
                                feeRequest.setPrice(crossProductPrice.getSalePrice());
                                feeRequest.setCurrency(crossProductPrice.getCurrency());
                            }
                            return feeRequest;
                        }

                ).collect(Collectors.toList());
        ApiRequest<List<ProductFeeEstimateRequest>> request = new ApiRequest<List<ProductFeeEstimateRequest>>()
                .setCrossPlatform(CrossPlatformEnum.of(sellPlatform.getCode()))
                .setShopIdInt(shopId)
                .setMarketId(marketId)
                .setRequest(feeEstimateRequests);

        ApiResponse<List<FeeModel>> response = financesApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                .getProductEstimatedFee(request);

        return response.success() ? response.getData() : Collections.emptyList();
    }


    @Override
    public FeeModel getProductEstimatedFee(Long productId) {
        CrossProductDO crossProduct = crossProductMapper.selectById(productId);
        if(crossProduct == null) {
            return null;
        }
        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(crossProduct.getPlatformId());

        ProductFeeEstimateRequest feeRequest = new ProductFeeEstimateRequest()
                .setSellerSku(crossProduct.getSellerSkuCode())
                .setFulfillType(FulfillTypeEnum.of(crossProduct.getFulfillType()))
                .setPlatformProductCode(crossProduct.getPlatformProductCode());
        CrossProductPriceDO crossProductPrice = crossPriceInventoryService.getEffectivePrice(crossProduct.getId());
        if (crossProductPrice != null) {
            feeRequest.setPrice(crossProductPrice.getSalePrice());
            feeRequest.setCurrency(crossProductPrice.getCurrency());
        } else {
            // 没有价格, 无法计算
            return null;
        }

        List<ProductFeeEstimateRequest> feeEstimateRequests = List.of(feeRequest);
        ApiRequest<List<ProductFeeEstimateRequest>> request = new ApiRequest<List<ProductFeeEstimateRequest>>()
                .setCrossPlatform(CrossPlatformEnum.of(sellPlatform.getCode()))
                .setShopIdInt(crossProduct.getShopId())
                .setMarketId(crossProduct.getMarketId())
                .setRequest(feeEstimateRequests);

        ApiResponse<List<FeeModel>> response = financesApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                .getProductEstimatedFee(request);

        return response.success() ? response.getData().get(0) : null;

    }
}
