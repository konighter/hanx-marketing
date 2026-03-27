package com.hzltd.module.amz.spapi.mapping.data;


import com.hzltd.module.amz.spapi.mapping.AttributeValueMapper;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.system.model.ProductSpuModel;

public class ItemNameMapper implements AttributeValueMapper {


    @Override
    public void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel) {
        categoryAttributeModel.setValue(spuModel.getName());
    }

    @Override
    public String getAttribute() {
        return "item_name";
    }
}
