package com.hzltd.module.erplus.service.cross;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.service.OrderApiFactory;
import com.hzltd.module.erplus.api.service.PricingInventoryApiFactory;
import com.hzltd.module.erplus.api.service.ProductApiFactory;
import com.hzltd.module.erplus.controller.admin.cross.vo.*;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.convert.cross.CrossPlatformProductConvert;
import com.hzltd.module.erplus.convert.cross.CrossProductListingConvert;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductAttrsDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductInventoryDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.cross.CrossProductInventoryMapper;
import com.hzltd.module.erplus.dal.mysql.cross.CrossProductMapper;
import com.hzltd.module.erplus.dal.mysql.cross.CrossProductPriceMapper;
import com.hzltd.module.erplus.dal.mysql.cross.ErpCrossProductAttrsMapper;
import com.hzltd.module.erplus.service.productpub.vo.CrossPlatformProductVO;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.spapi.enums.CrossListingStatus;
import com.hzltd.module.erplus.spapi.enums.CrossProductStatus;
import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.spapi.enums.LanguageEnum;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.common.FeeModel;
import com.hzltd.module.erplus.spapi.model.common.InventoryModel;
import com.hzltd.module.erplus.spapi.model.pricing.GetInventoryRequest;
import com.hzltd.module.erplus.spapi.model.product.MultiMarketProductModel;
import com.hzltd.module.erplus.spapi.model.product.ProductModel;
import com.hzltd.module.erplus.spapi.model.product.SearchProductRequest;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.PRODUCT_NOT_VALID;

@Slf4j
@Service
public class ErplusCrossProductServiceImpl implements ErplusCrossProductService {

    @Resource
    private CrossProductMapper crossProductMapper;

    @Resource
    private ErpCrossProductAttrsMapper crossProductAttrsMapper;

    @Resource
    private CrossProductPriceMapper crossProductPriceMapper;

    @Resource
    private CrossProductInventoryMapper inventoryMapper;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private ProductApiFactory productApiFactory;

    @Resource
    private OrderApiFactory orderApiFactory;

    @Resource
    private PricingInventoryApiFactory pricingInventoryApiFactory;

    @Resource
    private ErplusCrossPriceInventoryService crossProductPriceService;

    @Resource
    private ErplusFinancesService financesService;


    @Override
    public Optional<CrossPlatformProductVO> getCrossPlatformProduct(Long productId) {
        CrossProductDO crossPlatformProductDO = crossProductMapper.selectById(productId);

        FeeModel feeModel = financesService.getProductEstimatedFee(productId);


        log.info("estimatedFeeApiResponse: {}", feeModel);


//        if (crossPlatformProductDO == null) {
//            return Optional.empty();
//        }
//
//        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(crossPlatformProductDO.getPlatformId());
//        if (sellPlatform == null) {
//            return Optional.empty();
//        }

//        ApiResponse<MultiMarketProductModel> productModelApiResponse = productApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
//                        .getProduct(new ApiRequest<GetProductRequest>()
//                                .setShopIdInt(crossPlatformProductDO.getShopId())
//                                .setRequest(new GetProductRequest().setSellerSku(crossPlatformProductDO.getSellerSkuCode())));


//        CrossPlatformProductVO platformProductVO = CrossPlatformProductConvert.INSTANCE.convert(crossPlatformProductDO);
//
//        List<ErpCrossProductAttrsDO> productAttrsDOs = crossProductAttrsMapper.selectByProductId(productId);
//        if (CollectionUtils.isNotEmpty(productAttrsDOs)) {
//            List<ProductAttributeModel> attributeModels = CrossPlatformProductConvert.INSTANCE.convert(productAttrsDOs);
//            platformProductVO.setProductAttributes(attributeModels);
//        }

//        crossProductPriceService.getCrossProductOffer(productId);

//            crossProductPriceService.updateCrossPlatformProductPrice(new CrossProductPriceUpdateRequest()
//                    .setProductId(productId).addSalePrice(new PriceModel().setCurrency("USD").setAmount(new BigDecimal("6.98"))));

//        crossProductPriceService.getCrossInventory(productId);

//        orderApiFactory.getCrossApiService(CrossPlatformEnum.AMAZON)
//                .searchOrders(new ApiRequest<GetOrdersRequest>()
//                        .setShopIdInt(crossPlatformProductDO.getShopId())
//                        .setRequest(new GetOrdersRequest().setCreateTimeStart(LocalDateTime.now().minusDays(60))));


        return Optional.of(null);
    }

    @Override
    public Boolean mergeCrossProductVariation(CrossProductVariationMergeRequest request) {
        return null;
    }

    @Override
    public Optional<CrossProductDO> getBasicCrossPlatformProduct(Long productId) {
        return Optional.ofNullable(crossProductMapper.selectOne(new LambdaQueryWrapperX<CrossProductDO>().select(
                CrossProductDO::getId,
                CrossProductDO::getPlatformId,
                CrossProductDO::getShopId,
                CrossProductDO::getMarketId,
                CrossProductDO::getFulfillType,
                CrossProductDO::getTitle,
                CrossProductDO::getSellerSkuCode,
                CrossProductDO::getPlatformProductCode,
                CrossProductDO::getCategoryId,
                CrossProductDO::getBrand,
                CrossProductDO::getStatus
//                ErpCrossProductDO::getPublishStatus
        ).eq(CrossProductDO::getId, productId)));
    }

    @Override
    public Optional<CrossProductDO> getBasicCrossPlatformProductByPlatformIdAndProductId(Integer platformId, String sellerSku, String platformProductCode) {
        return Optional.ofNullable(crossProductMapper.selectOne(new LambdaQueryWrapperX<CrossProductDO>()
                .eqIfPresent(CrossProductDO::getPlatformId, platformId)
                .eqIfPresent(CrossProductDO::getSellerSkuCode, sellerSku)
                .eqIfPresent(CrossProductDO::getPlatformProductCode, platformProductCode).select(
                        CrossProductDO::getId,
                        CrossProductDO::getPlatformId,
                        CrossProductDO::getShopId,
                        CrossProductDO::getMarketId,
                        CrossProductDO::getFulfillType,
                        CrossProductDO::getTitle,
                        CrossProductDO::getSellerSkuCode,
                        CrossProductDO::getPlatformProductCode,
                        CrossProductDO::getCategoryId,
                        CrossProductDO::getBrand,
                        CrossProductDO::getStatus
//                ErpCrossProductDO::getPublishStatus
                )));
    }

    @Override
    public List<CrossProductDO> getBasicCrossProductBySkus(@NotNull Integer platformId, @NotNull Integer shopId, @NotNull String marketId, @NotEmpty List<String> skus) {
        return crossProductMapper.selectList(new LambdaQueryWrapperX<CrossProductDO>()
                .eqIfPresent(CrossProductDO::getPlatformId, platformId)
                .eqIfPresent(CrossProductDO::getShopId, shopId)
                .eqIfPresent(CrossProductDO::getMarketId, marketId)
                .inIfPresent(CrossProductDO::getSellerSkuCode, skus)
                .eq(CrossProductDO::getStatus, CrossListingStatus.ACTIVATE.getStatus()));
    }

    @Override
    public Optional<List<CrossProductDO>> getBasicCrossPlatformProduct(List<Long> productIds) {
        return Optional.ofNullable(crossProductMapper.selectList(new LambdaQueryWrapperX<CrossProductDO>().select(
                CrossProductDO::getId,
                CrossProductDO::getPlatformId,
                CrossProductDO::getShopId,
                CrossProductDO::getMarketId,
                CrossProductDO::getFulfillType,
                CrossProductDO::getTitle,
                CrossProductDO::getSellerSkuCode,
                CrossProductDO::getPlatformProductCode,
                CrossProductDO::getCategoryId,
                CrossProductDO::getBrand,
                CrossProductDO::getStatus,
                CrossProductDO::getMainImageUrl
//                ErpCrossProductDO::getPublishStatus
        ).in(CrossProductDO::getId, productIds)));
    }

    @Transactional
    @Override
    public Long saveCrossPlatformProduct(ProductPublishRequest product) {
        // 保存商品信息
        CrossProductDO crossPlatformProduct = CrossPlatformProductConvert.INSTANCE.convert(product);
        crossPlatformProduct.setStatus(CrossProductStatus.INIT.getStatus());
//        crossPlatformProduct.setPublishStatus(CrossProductPublishStatus.INIT.getStatus());
        crossProductMapper.insert(crossPlatformProduct);
        Long productId = crossPlatformProduct.getId();
        // 保存商品属性
        List<CrossProductAttrsDO> crossProductAttrs = CrossPlatformProductConvert.INSTANCE.convertProperties(productId, product.getProductAttributes());
        crossProductAttrsMapper.insertBatch(crossProductAttrs);

        if (!this.validCrossPlatformProduct(productId)) {
            throw new ServiceException(PRODUCT_NOT_VALID);
        }

        return productId;
    }

    @Override
    public Boolean validCrossPlatformProduct(Long productId) {
        return true;
    }


    @Async
    @Override
    public void syncProductListing(CrossProductSyncRequest request) {
        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(request.getPlatformId());
        if (sellPlatform == null) {
            return;
        }

        List<MultiMarketProductModel> multiMarketProducts = Lists.newArrayList();
        boolean hasNext = false;
        SearchProductRequest searchProductRequest = new SearchProductRequest().setIfAllContent(true);
        if (StringUtils.isNotEmpty(request.getSellerSkuCode())) {
            searchProductRequest.setSellerSkus(Collections.singletonList(request.getSellerSkuCode()));
        }
        if (StringUtils.isNotEmpty(request.getPlatformProductCode())) {
            searchProductRequest.setProductCodes(Collections.singletonList(request.getPlatformProductCode()));
        }
        // todo-- 处理时间

        searchProductRequest.setPageNo(request.getPageNo());
        do {
            ApiResponse<List<MultiMarketProductModel>> productApiResponse = productApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                    .searchProduct(new ApiRequest<SearchProductRequest>()
                            .setCrossPlatform(CrossPlatformEnum.of(sellPlatform.getCode()))
                            .setShopId(request.getShopId().toString())
                            .setLocale(Locale.SIMPLIFIED_CHINESE)
                            .setLanguage(LanguageEnum.ZH_CN)
                            .setMarketId(request.getMarketId())
                            .setTimestamp(System.currentTimeMillis())
                            .setRequest(searchProductRequest));
            if (CollectionUtils.isNotEmpty(productApiResponse.getData())) {
                multiMarketProducts.addAll(productApiResponse.getData());
            }
            hasNext = productApiResponse.hasNext();
            searchProductRequest.setPageNo(searchProductRequest.getPageNo() + 1);
        } while (hasNext);


        // 库存
        multiMarketProducts.forEach(productByMarket -> {
            productByMarket.forEach((market, product) -> {
                ApiRequest<GetInventoryRequest> productInventoryRequest = new ApiRequest<GetInventoryRequest>().setShopId(request.getShopId().toString())
                        .setMarketId(market).setRequest(new GetInventoryRequest().setSellerSkus(Collections.singletonList(product.getSellerSku())));
                ApiResponse<List<InventoryModel>> inventoryResponse = pricingInventoryApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                        .getInventory(productInventoryRequest);
                product.setInventory(inventoryResponse.getData());
            });
        });

        //Todo-- Issue: 处理库存更新问题


        multiMarketProducts.forEach(productModel -> {
            productModel.values().forEach(productModelItem -> {
                saveOrUpdateCrossPlatformProduct(request.getPlatformId(), request.getShopId(), productModelItem);
            });
        });


        log.info("syncProductListing success, platformId: {}, shopId: {}, total: {}", request.getPlatformId(), request.getShopId(), multiMarketProducts.size());
    }

    private void saveOrUpdateCrossPlatformProduct(Integer platformId, Integer shopId, ProductModel productModel) {

        CrossProductDO crossProductDO = crossProductMapper.selectOne(new LambdaQueryWrapperX<CrossProductDO>()
                .eqIfPresent(CrossProductDO::getPlatformId, platformId)
                .eqIfPresent(CrossProductDO::getShopId, shopId)
                .eqIfPresent(CrossProductDO::getPlatformProductCode, productModel.getProductCode())
                .eqIfPresent(CrossProductDO::getSellerSkuCode, productModel.getSellerSku()));
        Long version = Long.valueOf(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        if (crossProductDO == null) {
            crossProductDO = CrossProductListingConvert.INSTANCE.convert(productModel);
            crossProductDO.setPlatformId(platformId);
            crossProductDO.setShopId(shopId);
            crossProductDO.setVersion(version);
            crossProductMapper.insert(crossProductDO);
        } else {
            crossProductDO = CrossProductListingConvert.INSTANCE.update(productModel, crossProductDO);
            crossProductDO.setVersion(version);
            crossProductMapper.updateById(crossProductDO);
        }

        Long crossProductId = crossProductDO.getId();

        // todo -- 处理ralationShip和Issue, 价格库存, 其他不处理
        // 处理价格

        if (CollectionUtils.isNotEmpty(productModel.getPrices())) {
            productModel.getPrices().forEach(priceModel -> {
                CrossProductPriceDO priceDO = crossProductPriceMapper.selectOne(new LambdaQueryWrapperX<CrossProductPriceDO>()
                        .eqIfPresent(CrossProductPriceDO::getMarketId, productModel.getMarketId())
                        .eqIfPresent(CrossProductPriceDO::getShopId, shopId)
                        .eqIfPresent(CrossProductPriceDO::getProductId, crossProductId));
                if (priceDO == null) {
                    priceDO = CrossProductListingConvert.INSTANCE.convert(priceModel);
                    priceDO.setMarketId(productModel.getMarketId());
                    priceDO.setShopId(shopId);
                    priceDO.setProductId(crossProductId);
                    crossProductPriceMapper.insert(priceDO);
                } else {
                    priceDO = CrossProductListingConvert.INSTANCE.update(priceModel, priceDO);
                    crossProductPriceMapper.updateById(priceDO);
                }
            });
        }

        // 处理库存
        if (CollectionUtils.isNotEmpty(productModel.getInventory())) {
            productModel.getInventory().forEach(inventoryModel -> {
                CrossProductInventoryDO inventoryDO = inventoryMapper.selectOne(new LambdaQueryWrapperX<CrossProductInventoryDO>()
                        .eqIfPresent(CrossProductInventoryDO::getMarketId, productModel.getMarketId())
                        .eqIfPresent(CrossProductInventoryDO::getShopId, shopId)
                        .eqIfPresent(CrossProductInventoryDO::getProductId, crossProductId));

                if (inventoryDO == null) {
                    inventoryDO = CrossProductListingConvert.INSTANCE.convert(inventoryModel);
                    inventoryDO.setMarketId(productModel.getMarketId());
                    inventoryDO.setShopId(shopId);
                    inventoryDO.setProductId(crossProductId);
                    inventoryDO.setLastModifiedTimestamp(inventoryModel.getLastUpdateTime());
                    inventoryMapper.insert(inventoryDO);
                } else {
                    // 如何本地库存更新时间大于等于接口返回的更新时间，说明本地库存已经更新，无需更新
//                    if (inventoryDO.getLastModifiedTimestamp() >= inventoryModel.getLastUpdateTime()) {
//                        return;
//                    }
                    inventoryDO = CrossProductListingConvert.INSTANCE.update(inventoryModel, inventoryDO);
                    inventoryDO.setLastModifiedTimestamp(inventoryModel.getLastUpdateTime());
                    inventoryMapper.updateById(inventoryDO);
                }


            });
        }

    }

    /**
     * 查询跨境商品列表, 不同平台的组织方式不同，查询不同的层级
     * 1. 亚马逊平台，根据SKU查询
     * 2. Ozon平台，根据SKU查询
     * 3. 其他平台如TTS，根据SPU查询
     * 在列表页进行显示和操作的区分
     * @param request
     * @return
     */
    @Override
    public PageResult<?> getCrossPlatformProductPage(CrossProductPageRequest request) {
        SellPlatformDO sellPlatformDO = sellPlatformService.getSellPlatform(request.getPlatformId());

        // 亚马逊/Ozon平台，根据SKU查询
        if (CrossPlatformEnum.AMAZON.getCode().equalsIgnoreCase(sellPlatformDO.getCode()) || CrossPlatformEnum.OZON.getCode().equalsIgnoreCase(sellPlatformDO.getCode())) {
            return getCrossProductSkuPage(request);

        } else {
            // 其他平台，根据SPU查询
            return getCrossProductSpuPage(request);
        }
    }

    private PageResult<CrossProductListingResp> getCrossProductSkuPage(CrossProductPageRequest request) {
        PageResult<CrossProductDO> productPageResult = crossProductMapper.selectPage(request, new LambdaQueryWrapperX<CrossProductDO>()
                .eqIfPresent(CrossProductDO::getPlatformId, request.getPlatformId())
                .eqIfPresent(CrossProductDO::getShopId, request.getShopId())
                .inIfPresent(CrossProductDO::getStatus, request.getStatus())
                // 从请求时间到现在
                .betweenIfPresent(CrossProductDO::getUpdateTime, request.getUpdateTimeRange())
                .betweenIfPresent(CrossProductDO::getCreateTime, request.getCreateTimeRange())
                .likeIfPresent(CrossProductDO::getSellerSkuCode, request.getSellerSkuCode())
                .likeIfPresent(CrossProductDO::getPlatformProductCode, request.getPlatformProductCode())
                .eqIfPresent(CrossProductDO::getFulfillType, request.getFulfillType())
        );

        List<CrossProductInventoryDO> inventoryDOList = inventoryMapper.selectList(new LambdaQueryWrapperX<CrossProductInventoryDO>()
                .eqIfPresent(CrossProductInventoryDO::getMarketId, request.getMarketId())
                .eqIfPresent(CrossProductInventoryDO::getShopId, request.getShopId())
                .inIfPresent(CrossProductInventoryDO::getProductId, productPageResult.getList().stream().map(CrossProductDO::getId).collect(Collectors.toList()))
        );

        List<CrossProductPriceDO> priceDOList = crossProductPriceMapper.selectList(new LambdaQueryWrapperX<CrossProductPriceDO>()
                .eqIfPresent(CrossProductPriceDO::getMarketId, request.getMarketId())
                .eqIfPresent(CrossProductPriceDO::getShopId, request.getShopId())
                .inIfPresent(CrossProductPriceDO::getProductId, productPageResult.getList().stream().map(CrossProductDO::getId).collect(Collectors.toList()))
        );


        Map<Long, CrossProductInventoryDO> inventoryDOMap = inventoryDOList.stream().collect(Collectors.toMap(CrossProductInventoryDO::getProductId, Function.identity()));
        Multimap<Long, CrossProductPriceDO> priceDOMultimap = ArrayListMultimap.create();
        priceDOList.forEach(priceDO -> priceDOMultimap.put(priceDO.getProductId(), priceDO));


        // 转换为VO
        List<CrossProductListingResp> productListingRespVOS = productPageResult.getList().stream()
                .map(CrossProductListingConvert.INSTANCE::convert)
                .map(respVo -> {
                    // format
                    respVo.setFulfillTypeName(FulfillTypeEnum.of(respVo.getFulfillType()).getName());
                    respVo.setPlatformName(CrossPlatformEnum.valueOf(respVo.getPlatformId()).getName());
                    respVo.setStatusName(CrossListingStatus.of(respVo.getStatus()).getName());
                    // 合并库存信息
                    CrossProductInventoryDO inventoryDO = inventoryDOMap.get(respVo.getId());
                    if (inventoryDO != null) {
                        respVo.setInventory(inventoryDO);
                    }
                    // 合并价格信息
                    respVo.setPrice(priceDOMultimap.get(respVo.getId()));
                    return respVo;
                })
                .collect(Collectors.toList());

        PageResult<CrossProductListingResp> respVO = new PageResult<>();
        respVO.setList(productListingRespVOS);
        respVO.setTotal(productPageResult.getTotal());
        return respVO;
    }

    private PageResult<?> getCrossProductSpuPage(CrossProductPageRequest request) {
        return null;
    }

    @Override
    public Boolean addCrossProductVariation(CrossProductVariationAddRequest request) {
        return null;
    }

    @Override
    public void productChangeEventAction(ProductChangeEvent event) {

    }
}
