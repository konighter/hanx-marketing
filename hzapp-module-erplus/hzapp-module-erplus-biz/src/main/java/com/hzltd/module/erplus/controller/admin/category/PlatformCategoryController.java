package com.hzltd.module.erplus.controller.admin.category;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.category.vo.PlatformCategoryReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.PlatformCategoryRespVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ov/platform/category")
public class PlatformCategoryController {


    @RequestMapping("/list")
    public CommonResult<PlatformCategoryRespVO> getPlatformCategoryList(PlatformCategoryReqVO reqVO){
        return CommonResult.success(null);
    }





}
