package com.hzltd.module.amz.spapi.mapping.data;

import com.google.common.collect.Lists;
import com.hzltd.module.amz.spapi.mapping.AttributeSchemaMapper;
import com.hzltd.module.amz.spapi.mapping.AttributeValueMapper;
import com.hzltd.module.amz.spapi.proto.ProductTypeSchemaItem;
import com.hzltd.module.spapi.enums.AttributeTypeEnum;
import com.hzltd.module.spapi.model.category.AttributeValueModel;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.system.model.ProductSpuModel;
import org.apache.commons.collections4.CollectionUtils;

public class VariationThemeMapper implements AttributeValueMapper, AttributeSchemaMapper {

    @Override
    public void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel) {
        categoryAttributeModel.setAttrType(AttributeTypeEnum.SALES_PROPERTY);

        if (spuModel.getSpecType()) {
            categoryAttributeModel.setIsRequired(true);
        }

        categoryAttributeModel.setIsComposite(true);

    }

    public void mapAttributeSchema(CategoryAttributeModel categoryAttributeModel, ProductTypeSchemaItem schemaItem) {
        categoryAttributeModel.setAttrType(AttributeTypeEnum.SALES_PROPERTY);
        // 处理variation_theme的options
        if (CollectionUtils.isEmpty(categoryAttributeModel.getOptions())) {
            ProductTypeSchemaItem valueSchemaItem = schemaItem.getItems().getProperties().get("name");
            categoryAttributeModel.setOptions(Lists.newArrayList());
            for (int i = 0; i < valueSchemaItem.getEnumValues().size(); i++) {
                categoryAttributeModel.getOptions().add(AttributeValueModel.of(valueSchemaItem.getEnumValues().get(i), valueSchemaItem.getEnumNames().get(i)));
            }
        }
    }

    @Override
    public String getAttribute() {
        return "variation_theme";
    }
}
