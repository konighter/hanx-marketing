package com.hzltd.module.erplus.api.adptor.amz.mapping.data;

import com.hzltd.module.erplus.api.adptor.amz.mapping.AttributeValueMapper;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;

public class ProductDescriptionMapper implements AttributeValueMapper {
    @Override
    public void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel) {
        categoryAttributeModel.setValue(spuModel.getDescription());
    }

    @Override
    public String getAttribute() {
        return "product_description";
    }
}
