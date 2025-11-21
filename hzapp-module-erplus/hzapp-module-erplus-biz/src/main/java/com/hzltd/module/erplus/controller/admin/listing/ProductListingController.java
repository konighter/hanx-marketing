package com.hzltd.module.erplus.controller.admin.listing;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.listing.vo.ProductListingReqVO;
import com.hzltd.module.erplus.service.listing.ProductListingService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品刊登
 */
@RestController()
@RequestMapping("/erplus/listing")
public class ProductListingController {

    @Resource
    private ProductListingService productListingService;

    @PostMapping("/create")
    public CommonResult<String> createList(@RequestBody ProductListingReqVO reqVO) {
        productListingService.submitListing(reqVO);
        return CommonResult.success("");
    }


}
