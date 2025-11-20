package com.hzltd.module.erplus.controller.admin.category;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.constant.LanguageEnum;
import com.hzltd.module.erplus.controller.admin.category.vo.*;
import com.hzltd.module.erplus.service.category.CrossCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/erplus/cross/category")
public class CrossCategoryController {

    @Resource
    private CrossCategoryService crossCategoryService;

    @PostMapping("/list")
    public CommonResult<CrossCategoryRespVO> getCategories(@RequestBody CrossCategoryReqVO reqVO){

        List<CategoryVO> categories = crossCategoryService.getCrossCategoryList(reqVO);
        return CommonResult.success(new CrossCategoryRespVO(LanguageEnum.ZH_CN, categories));
    }


    @PostMapping("/attributes")
    public CommonResult<List<CategoryAttributeVO>> getCategoryAttribute( @RequestBody CrossCategoryAttrReqVO crossCategoryAttrReqVO) {
        return CommonResult.success(crossCategoryService.getCrossAttributeByCategory(crossCategoryAttrReqVO));
    }

    @PostMapping("/attributes/render")
    public CommonResult<List<CategoryAttributeVO>> renderCategoryAttribute( @RequestBody CrossCategoryAttrReqVO crossCategoryAttrReqVO) {
        return CommonResult.success(crossCategoryService.renderCategoryAttribute(crossCategoryAttrReqVO));
    }





}
