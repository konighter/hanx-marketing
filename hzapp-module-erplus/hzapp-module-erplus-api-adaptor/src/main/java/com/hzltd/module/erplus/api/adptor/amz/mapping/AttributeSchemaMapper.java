package com.hzltd.module.erplus.api.adptor.amz.mapping;

import com.hzltd.module.erplus.api.adptor.amz.proto.ProductTypeSchemaItem;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;

public interface AttributeSchemaMapper {
    void mapAttributeSchema(CategoryAttributeModel categoryAttributeModel, ProductTypeSchemaItem schemaItem);
}
