package com.hzltd.module.erplus.service.category;

import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;

import java.util.List;

public interface CategoryAttributeMappingApi {
  <T extends CategoryAttributeModel>  void mapCategoryAttributeValues(List<T> categoryAttributeModel, ProductSpuModel productSpuModel);
}
