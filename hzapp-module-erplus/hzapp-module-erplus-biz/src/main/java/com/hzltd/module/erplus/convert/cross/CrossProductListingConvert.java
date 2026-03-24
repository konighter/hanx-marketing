package com.hzltd.module.erplus.convert.cross;

import com.hzltd.module.erplus.controller.admin.cross.vo.CrossProductListingResp;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductInventoryDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import com.hzltd.module.spapi.model.common.Image;
import com.hzltd.module.spapi.model.common.InventoryModel;
import com.hzltd.module.spapi.model.common.PriceModel;
import com.hzltd.module.spapi.model.product.ProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface CrossProductListingConvert {

    CrossProductListingConvert INSTANCE = Mappers.getMapper(CrossProductListingConvert.class);

    default CrossProductListingResp convert(CrossProductDO crossProductDO) {
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

    default CrossProductDO convert(ProductModel productModel) {
        if (productModel == null) {
            return null;
        }
        CrossProductDO crossProductDO = new CrossProductDO();
        update(productModel, crossProductDO);
        return crossProductDO;
    }

    default CrossProductDO update(ProductModel productModel, CrossProductDO crossProductDO) {
        if (productModel == null) {
            return null;
        }
        if (crossProductDO == null) {
            crossProductDO = new CrossProductDO();
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


    default CrossProductInventoryDO convert(InventoryModel inventoryModel) {
        if (inventoryModel == null) {
            return null;
        }
        CrossProductInventoryDO crossProductInventoryDO = new CrossProductInventoryDO();
        return update(inventoryModel, crossProductInventoryDO);
    }

    default CrossProductInventoryDO update(InventoryModel inventoryModel, CrossProductInventoryDO crossProductInventoryDO) {
        if (inventoryModel == null) {
            return crossProductInventoryDO;
        }

        crossProductInventoryDO.setFnsku(inventoryModel.getFnSku());
        crossProductInventoryDO.setFulfillableQuantity(inventoryModel.getFulfillableQuantity());
        crossProductInventoryDO.setInboundWorkingQuantity(inventoryModel.getInboundWorkingQuantity());
        crossProductInventoryDO.setInboundShippedQuantity(inventoryModel.getInboundShippedQuantity());
        crossProductInventoryDO.setInboundReceivingQuantity(inventoryModel.getInboundReceivingQuantity());
        crossProductInventoryDO.setReservedQuantity(inventoryModel.getReservedQuantity());
        crossProductInventoryDO.setReservedPendingOrderQuantity(inventoryModel.getReservedPendingCustomerOrderQuantity());
        crossProductInventoryDO.setReservedTransshippingQuantity(inventoryModel.getReservedPendingTransshipmentQuantity());
        crossProductInventoryDO.setReservedFcprocessingQuantity(inventoryModel.getReservedFcProcessingQuantity());

        crossProductInventoryDO.setResearchingQuantity(inventoryModel.getResearchingQuantity());
        crossProductInventoryDO.setUnfulfillableQuantity(inventoryModel.getUnfulfillableQuantity());

        crossProductInventoryDO.setLastModifiedTimestamp(inventoryModel.getLastUpdateTime());
        return crossProductInventoryDO;
    }




    default CrossProductPriceDO convert(PriceModel priceModel) {
        if (priceModel == null) {
            return null;
        }
        CrossProductPriceDO priceDO = new CrossProductPriceDO();
        return update(priceModel, priceDO);
    }

    default CrossProductPriceDO update(PriceModel priceModel, CrossProductPriceDO priceDO) {
        if (priceModel == null) {
            return priceDO;
        }
        if (priceDO == null) {
            priceDO = new CrossProductPriceDO();
        }
        priceDO.setSalePrice(priceModel.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
        priceDO.setCurrency(priceModel.getCurrency());
        priceDO.setStartAt(priceModel.getStartAt());
        priceDO.setEndAt(priceModel.getEndAt());

        return priceDO;
    }


}
