package com.hzltd.module.erplus.convert.category;

import com.hzltd.module.erplus.controller.admin.category.vo.CategoryAttributeVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryVO;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.model.category.CategoryModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrossCategoryConvert {
    CrossCategoryConvert INSTANCE = Mappers.getMapper(CrossCategoryConvert.class);

    CategoryVO toCategoryVO(CategoryModel model);

    CategoryAttributeVO toCategoryAttrVO(CategoryAttributeModel model);
}
