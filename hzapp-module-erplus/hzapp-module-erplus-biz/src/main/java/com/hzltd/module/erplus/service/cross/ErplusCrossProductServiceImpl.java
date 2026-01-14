package com.hzltd.module.erplus.service.product;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.service.PricingInventoryApiFactory;
import com.hzltd.module.erplus.api.service.ProductApiFactory;
import com.hzltd.module.erplus.constant.FulfillTypeEnum;
import com.hzltd.module.erplus.constant.LanguageEnum;
import com.hzltd.module.erplus.controller.admin.product.vo.*;
import com.hzltd.module.erplus.controller.admin.product.vo.product.ErplusCrossProductListingRespVO;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.convert.product.CrossPlatformProductConvert;
import com.hzltd.module.erplus.convert.product.CrossProductListingConvert;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductAttrsDO;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.product.ErpCrossProductAttrsMapper;
import com.hzltd.module.erplus.dal.mysql.product.ErpCrossProductMapper;
import com.hzltd.module.erplus.enums.CrossListingStatus;
import com.hzltd.module.erplus.enums.CrossProductStatus;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.common.PriceModel;
import com.hzltd.module.erplus.model.product.MultiMarketProductModel;
import com.hzltd.module.erplus.model.product.ProductModel;
import com.hzltd.module.erplus.model.product.SearchProductRequest;
import com.hzltd.module.erplus.service.productpub.vo.CrossPlatformProductVO;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.hzltd.module.erplus.enums.ErrorCodeConstants.PRODUCT_NOT_VALID;

@Slf4j
@Service
public class ErplusCrossProductServiceImpl implements ErplusCrossProductService {

    @Resource
    private ErpCrossProductMapper crossProductMapper;

    @Resource
    private ErpCrossProductAttrsMapper crossProductAttrsMapper;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private ProductApiFactory productApiFactory;

    @Resource
    private PricingInventoryApiFactory pricingInventoryApiFactory;

    @Resource
    private ErplusCrossPriceInventoryService crossProductPriceService;


    @Override
    public Optional<CrossPlatformProductVO> getCrossPlatformProduct(Long productId) {
//        ErpCrossProductDO crossPlatformProductDO = crossProductMapper.selectById(productId);
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

        crossProductPriceService.getCrossInventory(productId);

        return Optional.of(null);
    }

    @Override
    public Boolean mergeCrossProductVariation(CrossProductVariationMergeRequest request) {
        return null;
    }

    @Override
    public Optional<ErpCrossProductDO> getBasicCrossPlatformProduct(Long productId) {
        return Optional.ofNullable(crossProductMapper.selectOne(new LambdaQueryWrapperX<ErpCrossProductDO>().select(
                ErpCrossProductDO::getId,
                ErpCrossProductDO::getPlatformId,
                ErpCrossProductDO::getShopId,
                ErpCrossProductDO::getMarketId,
                ErpCrossProductDO::getFulfillType,
                ErpCrossProductDO::getTitle,
                ErpCrossProductDO::getSellerSkuCode,
                ErpCrossProductDO::getPlatformProductCode,
                ErpCrossProductDO::getCategoryId,
                ErpCrossProductDO::getBrand,
                ErpCrossProductDO::getStatus
//                ErpCrossProductDO::getPublishStatus
        ).eq(ErpCrossProductDO::getId, productId)));
    }

    @Transactional
    @Override
    public Long saveCrossPlatformProduct(ProductPublishRequest product) {
        // 保存商品信息
        ErpCrossProductDO crossPlatformProduct = CrossPlatformProductConvert.INSTANCE.convert(product);
        crossPlatformProduct.setStatus(CrossProductStatus.INIT.getStatus());
//        crossPlatformProduct.setPublishStatus(CrossProductPublishStatus.INIT.getStatus());
        crossProductMapper.insert(crossPlatformProduct);
        Long productId = crossPlatformProduct.getId();
        // 保存商品属性
        List<ErpCrossProductAttrsDO> crossProductAttrs = CrossPlatformProductConvert.INSTANCE.convertProperties(productId, product.getProductAttributes());
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


        // 处理价格和库存

        multiMarketProducts.forEach(productByMaket -> {
            productByMaket.forEach((market, product) -> {
                ApiRequest<String> productOfferRequest = new ApiRequest<String>().setShopId(request.getShopId().toString())
                        .setMarketId(market).setRequest(product.getProductCode());
                pricingInventoryApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatform.getCode()))
                        .getItemOffers(productOfferRequest);
            });
        });




        multiMarketProducts.forEach(productModel -> {
            productModel.values().forEach(productModelItem -> {
                saveOrUpdateCrossPlatformProduct(request.getPlatformId(), request.getShopId(), productModelItem);
            });
        });






        log.info("syncProductListing success, platformId: {}, shopId: {}, total: {}", request.getPlatformId(), request.getShopId(), multiMarketProducts.size());
    }

    private void saveOrUpdateCrossPlatformProduct(Integer platformId, Integer shopId, ProductModel productModel) {

        ErpCrossProductDO crossProductDO = crossProductMapper.selectOne(new LambdaQueryWrapperX<ErpCrossProductDO>()
                .eqIfPresent(ErpCrossProductDO::getPlatformId, platformId)
                .eqIfPresent(ErpCrossProductDO::getShopId, shopId)
                .eqIfPresent(ErpCrossProductDO::getPlatformProductCode, productModel.getProductCode())
                .eqIfPresent(ErpCrossProductDO::getSellerSkuCode, productModel.getSellerSku()));
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

        // todo -- 处理ralationShip和Issue, 价格库存, 其他不处理


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

    private PageResult<ErplusCrossProductListingRespVO> getCrossProductSkuPage(CrossProductPageRequest request) {
        PageResult<ErpCrossProductDO> productPageResult = crossProductMapper.selectPage(request, new LambdaQueryWrapperX<ErpCrossProductDO>()
                .eqIfPresent(ErpCrossProductDO::getPlatformId, request.getPlatformId())
                .eqIfPresent(ErpCrossProductDO::getShopId, request.getShopId())
                .inIfPresent(ErpCrossProductDO::getStatus, request.getStatus())
                // 从请求时间到现在
                .betweenIfPresent(ErpCrossProductDO::getUpdateTime, request.getUpdateTimeRange())
                .betweenIfPresent(ErpCrossProductDO::getCreateTime, request.getCreateTimeRange())
                .likeIfPresent(ErpCrossProductDO::getSellerSkuCode, request.getSellerSkuCode())
                .likeIfPresent(ErpCrossProductDO::getPlatformProductCode, request.getPlatformProductCode())
                .eqIfPresent(ErpCrossProductDO::getFulfillType, request.getFulfillType())
        );

        // 转换为VO
        List<ErplusCrossProductListingRespVO> productListingRespVOS = productPageResult.getList().stream()
                .map(CrossProductListingConvert.INSTANCE::convert)
                .map(respVo -> {
                    // format
                    respVo.setFulfillTypeName(FulfillTypeEnum.of(respVo.getFulfillType()).getName());
                    respVo.setPlatformName(CrossPlatformEnum.valueOf(respVo.getPlatformId()).getName());
                    respVo.setStatusName(CrossListingStatus.of(respVo.getStatus()).getName());
                    return respVo;
                })
                .collect(Collectors.toList());

        PageResult<ErplusCrossProductListingRespVO> respVO = new PageResult<>();
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
