package com.hzltd.module.erplus.api.adptor.amz.mapping;

import com.hzltd.module.erplus.api.adptor.amz.mapping.data.OriginCountryMapper;
import com.hzltd.module.erplus.api.adptor.amz.mapping.data.SupplierDeclaredDGHXRegulationMapper;
import com.hzltd.module.erplus.api.adptor.amz.mapping.data.VariationThemeMapper;
import com.hzltd.module.erplus.api.adptor.amz.proto.ProductTypeSchemaItem;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AmzAttributeSchemaMappingService {
    private static Map<String, AttributeSchemaMapper> mappers = Map.of(
            "variation_theme", new VariationThemeMapper(),
            "supplier_declared_dg_hz_regulation", new SupplierDeclaredDGHXRegulationMapper(),
            "country_of_origin", new OriginCountryMapper()
    );

    public void doMapAttributeSchema(CategoryAttributeModel categoryAttributeModel, ProductTypeSchemaItem schemaItem) {
        AttributeSchemaMapper mapper = mappers.get(categoryAttributeModel.getAttrCode());
        if (mapper == null) {
            return;
        }
        mapper.mapAttributeSchema(categoryAttributeModel, schemaItem);
    }


}
