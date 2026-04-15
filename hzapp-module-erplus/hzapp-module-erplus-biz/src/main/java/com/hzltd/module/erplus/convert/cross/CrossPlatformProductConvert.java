package com.hzltd.module.erplus.convert.cross;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductAttrsDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.service.productpub.vo.CrossPlatformProductVO;
import com.hzltd.module.erplus.spapi.model.common.ProductAttributeModel;
import com.hzltd.module.erplus.spapi.model.product.CreateProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CrossPlatformProductConvert {
    CrossPlatformProductConvert INSTANCE = Mappers.getMapper(CrossPlatformProductConvert.class);

    default CrossProductDO convert(CreateProductRequest product) {
       return CrossProductDO.builder()
                .platformId(product.getCrossPlatform().getValue())
                .shopId(product.getShopId())
                .marketId(product.getMarketId())
                .fulfillType(product.getFulfillType().getCode())
                .sellerSkuCode(product.getRelateProductId().toString())
//                .specType(product.getSpecType())
                .title(product.getTitle())
                .keyword(product.getKeywords())
                .features(JsonUtils.toJsonString(product.getFeatures()))
                .description(product.getDescription())
                .categoryId(product.getCategory().getCategoryId())
                .brand(product.getBrand() == null ? "" : product.getBrand().getName())
                .mainImageUrl(JsonUtils.toJsonString(product.getMainImage()))
                .sliderImageUrls(JsonUtils.toJsonString(product.getSliderImages()))
                .videoUrl(JsonUtils.toJsonString(product.getVideo()))
                .dimension(JsonUtils.toJsonString(product.getDimension()))
//                .security(JsonUtils.toJsonString(product.getSecurity()))
                .extra(JsonUtils.toJsonString(product.getCrossPlatformExtAttrs()))
                .build();
    }

    default CrossProductDO convert(ProductPublishRequest product) {
        return new CrossProductDO();
    }

    default List<CrossProductAttrsDO> convertProperties(Long productId, List<ProductAttributeModel> productAttributes) {
        return productAttributes.stream().map(attr -> {
            return CrossProductAttrsDO.builder()
                    .productId(productId)
                    .attrId(attr.getAttrId())
                    .attrName(attr.getAttrName())
                    .attrValue(JsonUtils.toJsonString(attr.getAttrValues()))
                    .build();
        }).collect(Collectors.toList());


    }

    default CrossPlatformProductVO convert(CrossProductDO productDO) {
        return null;
    }

    default List<ProductAttributeModel> convert(List<CrossProductAttrsDO> productAttrDOs) {
        return null;
    }

}

