package com.hzltd.module.amz.api.spapi.mapping;

import com.hzltd.module.amz.api.spapi.proto.ProductTypeSchemaItem;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;

public interface AttributeSchemaMapper {
    void mapAttributeSchema(CategoryAttributeModel categoryAttributeModel, ProductTypeSchemaItem schemaItem);
}
