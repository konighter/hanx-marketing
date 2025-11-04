package com.hzltd.module.erplus.service.category;

import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.module.erplus.api.service.CrossApiServiceFactory;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryAttributeVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryAttrReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryReqVO;
import com.hzltd.module.erplus.convert.category.CrossCategoryConvert;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.model.category.CategoryModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrossCategoryServiceImpl implements CrossCategoryService {

    private CrossApiServiceFactory crossApiServiceFactory;

    @Override
    public List<CategoryVO> getCrossCategoryList(CrossCategoryReqVO reqVO) {
        CategoryApi categoryApi = crossApiServiceFactory.getCategoryApi(reqVO.getCrossPlatform());
        ApiResponse<List<CategoryModel>> apiResponse = categoryApi.getCategories(null);
        if (!apiResponse.success()) {
            throw new ServiceException(apiResponse.getCode(), apiResponse.getMsg());
        }

        return apiResponse.getData().stream().map(CrossCategoryConvert.INSTANCE::toCategoryVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryAttributeVO> getCrossAttributeByCategory(CrossCategoryAttrReqVO reqVO) {

        CategoryApi categoryApi = crossApiServiceFactory.getCategoryApi(reqVO.getCrossPlatform());
        ApiResponse<List<CategoryAttributeModel>> apiResponse = categoryApi.getCategoryAttributes(null);
        if (!apiResponse.success()) {
            throw new ServiceException(apiResponse.getCode(), apiResponse.getMsg());
        }

        return apiResponse.getData().stream().map(CrossCategoryConvert.INSTANCE::toCategoryAttrVO).collect(Collectors.toList());
    }
}
