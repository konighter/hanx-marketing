package com.hzltd.module.spapi.service.category;

import com.hzltd.module.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.system.model.ProductSpuModel;

import java.util.List;

public interface CategoryAttributeMappingApi {
  <T extends CategoryAttributeModel>  void mapCategoryAttributeValues(List<T> categoryAttributeModel, ProductSpuModel productSpuModel);
}
