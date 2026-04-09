package com.hzltd.module.erplus.controller.admin.productpub;


import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishV2Request;
import com.hzltd.module.erplus.service.productpub.ProductPublishService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Slf4j
@Validated
@RestController
@RequestMapping("/erplus/product")
public class ProductPublishController {

    @Resource
    private ProductPublishService productPublishService;

    @PostMapping("/pub")
    @Operation(summary = "刊登商品")
    @PreAuthorize("@ss.hasPermission('product:publish:create')")
    public CommonResult<ProductPublishResponse> publishProduct(@Valid @RequestBody ProductPublishRequest request) {
        ProductPublishResponse response = null;
        try {
            response = productPublishService.publishProduct(request, getLoginUserId());
        } catch (ServiceException e) {
            log.error("PublishProduct Error", e);
            return CommonResult.error(e);
        }

        return CommonResult.success(response);
    }

    @PostMapping("/pub-v2")
    @Operation(summary = "刊登商品 (V2 SKU维度)")
    @PreAuthorize("@ss.hasPermission('product:publish:create')")
    public CommonResult<ProductPublishResponse> publishProductV2(@Valid @RequestBody ProductPublishV2Request request) {
        ProductPublishResponse response = productPublishService.publishProductV2(request, getLoginUserId());
        return CommonResult.success(response);
    }

    @GetMapping("/page1")
    @Operation(summary = "分页查询刊登商品")
    public CommonResult queryProductPage() {
        return CommonResult.success(null);
    }







}
