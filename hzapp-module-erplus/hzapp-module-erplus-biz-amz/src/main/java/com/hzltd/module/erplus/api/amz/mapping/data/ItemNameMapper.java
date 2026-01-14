package com.hzltd.module.erplus.api.amz.mapping.data;


import com.hzltd.module.erplus.api.amz.mapping.AttributeValueMapper;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;

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
