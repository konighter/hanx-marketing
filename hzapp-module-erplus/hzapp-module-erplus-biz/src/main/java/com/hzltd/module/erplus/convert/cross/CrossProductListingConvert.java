package com.hzltd.module.erplus.convert.product;

import com.hzltd.module.erplus.controller.admin.cross.vo.CrossProductListingResp;
import com.hzltd.module.erplus.dal.dataobject.cross.ErpCrossProductDO;
import com.hzltd.module.erplus.model.common.Image;
import com.hzltd.module.erplus.model.product.ProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrossProductListingConvert {

    CrossProductListingConvert INSTANCE = Mappers.getMapper(CrossProductListingConvert.class);

    default CrossProductListingResp convert(ErpCrossProductDO crossProductDO) {
        if (crossProductDO == null) {
            return null;
        }
        CrossProductListingResp respVO = new CrossProductListingResp();
        respVO.setId(crossProductDO.getId());
        respVO.setPlatformProductCode(crossProductDO.getPlatformProductCode());
        respVO.setSellerProductCode(crossProductDO.getSellerSkuCode());
        respVO.setTitle(crossProductDO.getTitle());
        respVO.setMainImage(new Image().setUrl(crossProductDO.getMainImageUrl()));
        respVO.setPlatformId(crossProductDO.getPlatformId());
        respVO.setCategoryCode(crossProductDO.getCategoryId());
        respVO.setBrand(crossProductDO.getBrand());
        respVO.setFulfillType(crossProductDO.getFulfillType());
        respVO.setStatus(crossProductDO.getStatus());
        respVO.setCreateTime(crossProductDO.getCreateTime());
        respVO.setUpdateTime(crossProductDO.getUpdateTime());
        return respVO;
    }

    default ErpCrossProductDO convert(ProductModel productModel) {
        if (productModel == null) {
            return null;
        }
        ErpCrossProductDO crossProductDO = new ErpCrossProductDO();
        update(productModel, crossProductDO);
        return crossProductDO;
    }

    default ErpCrossProductDO update(ProductModel productModel, ErpCrossProductDO crossProductDO) {
        if (productModel == null) {
            return null;
        }
        if (crossProductDO == null) {
            crossProductDO = new ErpCrossProductDO();
        }
        crossProductDO.setPlatformProductCode(productModel.getProductCode());
        crossProductDO.setSellerSkuCode(productModel.getSellerSku());
        crossProductDO.setMarketId(productModel.getMarketId());
        crossProductDO.setCategoryId(productModel.getCategory());
        crossProductDO.setBrand(productModel.getBrand());
        if (productModel.getFulfillType() != null) {
            crossProductDO.setFulfillType(productModel.getFulfillType().getCode());
        }
        crossProductDO.setTitle(productModel.getProductName());
        crossProductDO.setDescription(productModel.getProductDescription());
        if (productModel.getMainImage() != null) {
            crossProductDO.setMainImageUrl(productModel.getMainImage().getUrl());
        }
        crossProductDO.setStatus(productModel.getStatus().getStatus());
        crossProductDO.setCreateTime(productModel.getCreateTime());
        crossProductDO.setUpdateTime(productModel.getUpdateTime());
        return crossProductDO;
    }
}
