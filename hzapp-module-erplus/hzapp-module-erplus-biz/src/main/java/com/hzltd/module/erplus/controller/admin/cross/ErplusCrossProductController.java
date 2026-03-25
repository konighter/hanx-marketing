package com.hzltd.module.erplus.controller.admin.cross;

import com.hzltd.module.erplus.controller.admin.cross.vo.*;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.erplus.service.cross.ErplusCrossPriceInventoryService;
import com.hzltd.module.erplus.service.cross.ErplusCrossProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/erplus/cross-product")
public class ErplusCrossProductController {

    @Resource
    private ErplusCrossProductService crossProductService;

    @Resource
    private ErplusCrossPriceInventoryService crossProductPriceService;


    /**
     * 查询跨境商品
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public ApiResponse<?> getCrossProductDetail(Long id) {
        return ApiResponse.success(crossProductService.getCrossPlatformProduct(id).get());
    }

    /**
     * 查询跨境商品
     * @param request
     * @return
     */
    @PostMapping("/page")
    public ApiResponse<?> getCrossProductPage(@RequestBody CrossProductPageRequest request) {
        return ApiResponse.success(crossProductService.getCrossPlatformProductPage(request));
    }

    /**
     * 查询跨境商品
     * @param request
     * @return
     */
    @PostMapping("/sync")
    public ApiResponse<?> syncProductListing(@RequestBody CrossProductSyncRequest request) {
        crossProductService.syncProductListing(request);
        return ApiResponse.success(true);
    }

    /**
     * 更新跨境商品价格
     * @param request
     * @return
     */
    @PostMapping("/price/update")
    public ApiResponse<Boolean> updateCrossPlatformProductPrice(@RequestBody CrossProductPriceUpdateRequest request) {
        return ApiResponse.success(crossProductPriceService.updateCrossPlatformProductPrice(request));
    }

    /**
     * 合并跨境商品属性
     * @param request
     * @return
     */
    @PostMapping("/variation/merge")
    public ApiResponse<Boolean> mergeCrossProductVariation(@RequestBody CrossProductVariationMergeRequest request) {
        return ApiResponse.success(crossProductService.mergeCrossProductVariation(request));
    }

    /**
     * 添加跨境商品属性
     * @param request
     * @return
     */
    @PostMapping("/variation/add")
    public ApiResponse<Boolean> addCrossProductVariation(@RequestBody CrossProductVariationAddRequest request) {
        return ApiResponse.success(crossProductService.addCrossProductVariation(request));
    }


}
