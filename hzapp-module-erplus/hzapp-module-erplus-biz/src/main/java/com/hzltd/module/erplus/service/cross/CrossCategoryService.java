package com.hzltd.module.erplus.service.category;

import com.hzltd.module.erplus.controller.admin.category.vo.CategoryAttributeVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryAttrReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryReqVO;

import java.util.List;

public interface CrossCategoryService {

    List<CategoryVO> getCrossCategoryList(CrossCategoryReqVO reqVO);

    List<CategoryAttributeVO> getCrossAttributeByCategory(CrossCategoryAttrReqVO platformCategoryAttrReqVO);

    List<CategoryAttributeVO> renderCategoryAttribute(CrossCategoryAttrReqVO crossCategoryAttrReqVO);

}
