package com.hzltd.module.amz.api.spapi.mapping.data;

import com.hzltd.module.amz.api.spapi.mapping.AttributeValueMapper;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.spapi.model.system.ProductSpuModel;

public class ProductTypeMapper implements AttributeValueMapper {
    @Override
    public void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel) {
        categoryAttributeModel.setValue(spuModel.getCrossCategory());
        categoryAttributeModel.setIsEditable(Boolean.FALSE);
    }

    @Override
    public String getAttribute() {
        return "item_type_keyword";
    }
}
