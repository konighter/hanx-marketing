package com.hzltd.module.amz.spapi.mapping.data;

import com.hzltd.module.amz.spapi.mapping.AttributeValueMapper;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.system.model.ProductSpuModel;

public class BrandMapper implements AttributeValueMapper {

    @Override
    public void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel) {
        categoryAttributeModel.setValue(spuModel.getBrandModel().getCode());
    }

    @Override
    public String getAttribute() {
        return "brand";
    }
}
