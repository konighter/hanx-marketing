package com.hzltd.module.erplus.dal.mysql.productpub;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.productpub.ProductListingDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 跨境商品刊登状态 Mapper
 *
 * @author Antigravity
 */
@Mapper
public interface ProductListingMapper extends BaseMapperX<ProductListingDO> {

    default ProductListingDO selectByProductId(Long productId) {
        return selectOne(new LambdaQueryWrapperX<ProductListingDO>()
                .eq(ProductListingDO::getProductId, productId));
    }

    default ProductListingDO selectBySku(Integer platformId, Integer shopId, String marketId, String sellerSku) {
        return selectOne(new LambdaQueryWrapperX<ProductListingDO>()
                .eq(ProductListingDO::getPlatformId, platformId)
                .eq(ProductListingDO::getShopId, shopId)
                .eq(ProductListingDO::getMarketId, marketId)
                .eq(ProductListingDO::getSellerSku, sellerSku));
    }

}
