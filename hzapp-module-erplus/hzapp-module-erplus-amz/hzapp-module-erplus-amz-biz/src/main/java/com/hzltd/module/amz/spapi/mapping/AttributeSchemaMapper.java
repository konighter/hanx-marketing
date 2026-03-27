package com.hzltd.module.amz.spapi.mapping;

import com.hzltd.module.amz.spapi.proto.ProductTypeSchemaItem;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;

public interface AttributeSchemaMapper {
    void mapAttributeSchema(CategoryAttributeModel categoryAttributeModel, ProductTypeSchemaItem schemaItem);
}
