package com.hzltd.module.erplus.controller.admin.category;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.category.vo.*;
import com.hzltd.module.erplus.service.category.CrossCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erplus/cross/category")
public class CrossCategoryController {

    @Resource
    private CrossCategoryService crossCategoryService;

    @RequestMapping("/list")
    public CommonResult<CrossCategoryRespVO> getCategories(CrossCategoryReqVO reqVO){
        List<CategoryVO> categories = crossCategoryService.getCrossCategoryList(reqVO);
        return CommonResult.success(new CrossCategoryRespVO(reqVO.getLanguage(), categories));
    }


    @GetMapping("/attributes")
    public CommonResult<List<CategoryAttributeVO>> getCategoryAttribute(CrossCategoryAttrReqVO crossCategoryAttrReqVO) {
        return CommonResult.success(crossCategoryService.getCrossAttributeByCategory(crossCategoryAttrReqVO));
    }




}
