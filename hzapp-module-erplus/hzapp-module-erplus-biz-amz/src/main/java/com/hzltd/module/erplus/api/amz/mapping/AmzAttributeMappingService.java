package com.hzltd.module.erplus.api.amz.mapping;

import com.hzltd.module.erplus.api.amz.mapping.data.*;
import com.hzltd.module.erplus.api.annotations.ServiceRegister;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.service.category.CategoryAttributeMappingApi;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = CategoryAttributeMappingApi.class)
public class AmzAttributeMappingService implements CategoryAttributeMappingApi {

    private Map<String, AttributeValueMapper> attributeValueMappers;

    public AmzAttributeMappingService() {
        this.attributeValueMappers = Map.of(
                "item_name", new ItemNameMapper(),
                "brand", new BrandMapper(),
                "bullet_point", new BulletPointMapper(),
                "product_description", new ProductDescriptionMapper(),
                "item_type_keyword", new ProductTypeMapper(),
                "country_of_origin", new OriginCountryMapper(),
                "supplier_declared_dg_hz_regulation", new SupplierDeclaredDGHXRegulationMapper(),
                "variation_theme", new VariationThemeMapper()
        );
    }

    /**
     * 映射分类属性值到产品SPU模型
     *
     * @param categoryAttributeModel 分类属性模型列表
     * @param productSpuModel        产品SPU模型
     */
     @Override
    public <T extends CategoryAttributeModel> void mapCategoryAttributeValues(List<T> categoryAttributeModel, ProductSpuModel productSpuModel) {
        categoryAttributeModel.forEach(categoryAttribute -> {
            String attribute = categoryAttribute.getAttrCode();
            AttributeValueMapper mapper = attributeValueMappers.get(attribute);
            if (mapper == null) {
                return;
            }
            try {
                mapper.mapAttributeValue(productSpuModel, categoryAttribute);
            } catch (Exception e) {
                log.error("Error mapping attribute {} for product: {}", attribute, productSpuModel.getId(), e);
            }

        });

    }


}
