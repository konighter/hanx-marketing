package com.hzltd.module.erplus.api.adptor.amz.mapping.data;

import com.hzltd.module.erplus.api.adptor.amz.mapping.AttributeValueMapper;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;

import java.util.Map;

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
