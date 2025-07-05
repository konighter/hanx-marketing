package com.hzltd.module.erplus.service.spu;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.google.common.collect.Lists;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.spu.vo.*;
import com.hzltd.module.erplus.enums.ErrorCodeConstants;
import com.hzltd.module.erplus.enums.ProductClaimStatus;
import com.hzltd.module.erplus.convert.spu.ProductClaimConvert;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductClaimDO;
import com.hzltd.module.erplus.dal.mysql.spu.ProductClaimMapper;
import com.hzltd.module.erplus.service.brand.ProductBrandService;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.service.sellzone.SellZoneService;
import com.hzltd.module.erplus.service.shop.ShopService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;


import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

/**
 * 商品认领 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class ProductClaimServiceImpl implements ProductClaimService {

    @Resource
    private ProductClaimMapper productClaimMapper;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private SellZoneService sellZoneService;

    @Resource
    private ProductBrandService brandService;

    @Resource
    private ShopService shopService;

    @Override
    public Integer createProductClaim(ProductClaimSaveReqVO createReqVO) {
        // 插入
        ProductClaimDO productClaim = BeanUtils.toBean(createReqVO, ProductClaimDO.class);
        productClaimMapper.insert(productClaim);
        // 返回
        return productClaim.getId();
    }

    @Override
    @DSTransactional
    public List<Integer> batchCreateProductClaim(ProductClaimBatchReqVO createReqVO) {

        List<Integer> claimIds = Lists.newArrayList();
        // AMZ 一次只能发一个SKU
        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(createReqVO.getPlatform());
        if (sellPlatform.getCode().equals("AMZ") && createReqVO.getSkus().size() > 1) {
            throw  exception(PRODUCT_CLAIM_SKU_RULE_PLT, "亚马逊单次只能认领一个SKU");
        }

        for (Integer  shopId : createReqVO.getShopId()) {

            if (existClaimSku(shopId, createReqVO.getSpuId(), createReqVO.getSkus())) {
                ShopDO shop = shopService.getShop(shopId);
                throw exception(ErrorCodeConstants.PRODUCT_CLAIM_SKU_EXISTS, StrUtil.format("店铺[{}]已认领SKU, 请检查后重试", shop.getName()));
            }

            ProductClaimDO productClaimDO = ProductClaimConvert.INSTANCE.convert(createReqVO);
            productClaimDO.setShopId(shopId);
            productClaimDO.setStatus(ProductClaimStatus.CLAIM.getStatus());
            productClaimMapper.insert(productClaimDO);
            claimIds.add(productClaimDO.getId());
        }

        return claimIds;
    }

    /**
     * 是否存在已认领和已同步的SKU
     * @param shopId
     * @param spuId
     * @param skuInfo
     * @return
     */
    private boolean existClaimSku(Integer shopId, Long spuId, List<ProductSkuVO> skuInfo) {
        if (CollectionUtils.isEmpty(skuInfo)) {
            return true;
        }
        List<ProductClaimDO> productClaims = productClaimMapper.selectList(new LambdaQueryWrapperX<ProductClaimDO>()
                .eqIfPresent(ProductClaimDO::getShopId, shopId)
                .eqIfPresent(ProductClaimDO::getSpuId, spuId)
                .inIfPresent(ProductClaimDO::getStatus, ProductClaimStatus.CLAIM.getStatus(), ProductClaimStatus.SYNCED.getStatus()));

        Map<Long, ProductSkuVO> skuMap = productClaims.stream().flatMap(p -> JsonUtils.parseArray(p.getSkuInfo(), ProductSkuVO.class).stream()).collect(Collectors.toMap(ProductSkuVO::getId, Function.identity(), (a, b) -> a));

        return skuInfo.stream().anyMatch(p -> skuMap.containsKey(p.getId()));

    }

    @Override
    public void updateProductClaim(ProductClaimSaveReqVO updateReqVO) {
        // 校验存在
        validateProductClaimExists(updateReqVO.getId());
        // 更新
        ProductClaimDO updateObj = BeanUtils.toBean(updateReqVO, ProductClaimDO.class);
        productClaimMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductClaim(Integer id) {
        // 校验存在
        validateProductClaimExists(id);
        // 删除
        productClaimMapper.deleteById(id);
    }

    private void validateProductClaimExists(Integer id) {
        if (productClaimMapper.selectById(id) == null) {
            throw exception(PRODUCT_CLAIM_NOT_EXISTS);
        }
    }

    @Override
    public ProductClaimDO getProductClaimDetail(Integer id) {
        return productClaimMapper.selectById(id);
    }

    @Override
    public List<ProductClaimDO> getProductClaimBatch(List<Integer> ids) {
        return productClaimMapper.selectList(new LambdaQueryWrapperX<ProductClaimDO>().inIfPresent(ProductClaimDO::getId, ids));
    }

    @Override
    public ProductClaimRespVO getProductClaim(Integer id) {
        ProductClaimDO productClaim = this.getProductClaimDetail(id);
        return BeanUtils.toBean(productClaim, ProductClaimRespVO.class, (respVO) -> {
            respVO.setPlatformName(sellPlatformService.getSellPlatform(respVO.getPlatform()).getName());
            respVO.setSellZoneName(sellZoneService.getSellZone(respVO.getSellZone()).getZoneName());
            respVO.setBrandName(brandService.getBrandCache(respVO.getBrandId()).getName());
            respVO.setSkus(JsonUtils.parseArray(respVO.getSkuInfo(), ProductSkuVO.class));
        });

    }

    @Override
    public PageResult<ProductClaimRespVO> getProductClaimPage(ProductClaimPageReqVO pageReqVO) {
        PageResult<ProductClaimDO> result = productClaimMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(result, ProductClaimRespVO.class, (respVO) -> {
            respVO.setPlatformName(sellPlatformService.getSellPlatform(respVO.getPlatform()).getName());
            respVO.setSellZoneName(sellZoneService.getSellZone(respVO.getSellZone()).getZoneName());
            respVO.setBrandName(brandService.getBrandCache(respVO.getBrandId()).getName());
            respVO.setSkus(JsonUtils.parseArray(respVO.getSkuInfo(), ProductSkuVO.class));
        });
    }

    @Async
    @Override
    public void syncProductClaim(ProductClaimDO productClaim) {

    }

    @Async
    @Override
    public void syncProductClaimBatch(List<ProductClaimDO> productClaims) {

    }
}