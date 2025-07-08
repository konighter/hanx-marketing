package com.hzltd.module.erplus.convert.productpub;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erpls.api.model.common.ProductAttributeModel;
import com.hzltd.module.erpls.api.model.product.CreateProductRequest;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductAttrsDO;
import com.hzltd.module.erplus.service.productpub.vo.CrossPlatformProductVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CrossPlatformProductConvert {
    CrossPlatformProductConvert INSTANCE = Mappers.getMapper(CrossPlatformProductConvert.class);

    default ErpCrossProductDO convert(CreateProductRequest product) {
       return ErpCrossProductDO.builder()
                .platformId(product.getCrossPlatform().getValue())
                .shopId(product.getShopId())
                .marketId(product.getMarketId())
                .fulfillType(product.getFulfillType().getCode())
                .language(product.getLanguage().getCode())
                .relateProductId(product.getRelateProductId())
                .specType(product.getSpecType())
                .title(product.getTitle())
                .keyword(product.getKeywords())
                .features(JsonUtils.toJsonString(product.getFeatures()))
                .description(product.getDescription())
                .categoryId(product.getCategory().getCategoryId())
                .brandId(product.getBrand() == null ? "" : product.getBrand().getId())
                .mainImageUrl(JsonUtils.toJsonString(product.getMainImage()))
                .sliderImageUrls(JsonUtils.toJsonString(product.getSliderImages()))
                .videoUrl(JsonUtils.toJsonString(product.getVideo()))
                .dimension(JsonUtils.toJsonString(product.getDimension()))
                .security(JsonUtils.toJsonString(product.getSecurity()))
                .extra(JsonUtils.toJsonString(product.getCrossPlatformExtAttrs()))
                .build();
    }

    default List<ErpCrossProductAttrsDO> convertProperties(Long productId, List<ProductAttributeModel> productAttributes) {
        return productAttributes.stream().map(attr -> {
            return ErpCrossProductAttrsDO.builder()
                    .productId(productId)
                    .attrId(attr.getAttrId())
                    .attrName(attr.getAttrName())
                    .attrValue(JsonUtils.toJsonString(attr.getAttrValues()))
                    .build();
        }).collect(Collectors.toList());


    }

    default CrossPlatformProductVO convert(ErpCrossProductDO productDO) {
        return null;
    }

    default List<ProductAttributeModel> convert(List<ErpCrossProductAttrsDO> productAttrDOs) {
        return null;
    }

}

