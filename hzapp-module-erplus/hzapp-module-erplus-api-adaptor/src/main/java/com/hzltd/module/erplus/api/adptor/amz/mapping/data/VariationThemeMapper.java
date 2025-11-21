package com.hzltd.module.erplus.api.adptor.amz.mapping.data;

import com.google.common.collect.Lists;
import com.hzltd.module.erplus.api.adptor.amz.mapping.AttributeSchemaMapper;
import com.hzltd.module.erplus.api.adptor.amz.mapping.AttributeValueMapper;
import com.hzltd.module.erplus.api.adptor.amz.proto.ProductTypeSchemaItem;
import com.hzltd.module.erplus.constant.AttributeTypeEnum;
import com.hzltd.module.erplus.model.category.AttributeValueModel;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

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
