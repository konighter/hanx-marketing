package com.hzltd.module.erplus.spapi.service.category;

import com.hzltd.module.erplus.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.system.model.ProductSpuModel;

import java.util.List;

public interface CategoryAttributeMappingApi {
  <T extends CategoryAttributeModel>  void mapCategoryAttributeValues(List<T> categoryAttributeModel, ProductSpuModel productSpuModel);
}
