package com.hzltd.module.erplus.api.adptor.amz.mapping.data;

import com.hzltd.module.erplus.api.adptor.amz.mapping.AttributeValueMapper;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;

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
