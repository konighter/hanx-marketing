package com.hzltd.module.erplus.controller.admin.productpub;


import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;
import com.hzltd.module.erplus.service.productpub.ProductPublishServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/erplus/product/pub")
public class ProductPublishController {

    private ProductPublishServiceImpl productPublishService;

    @PostMapping("/")
    @Operation(summary = "刊登商品")
    @PreAuthorize("@ss.hasPermission('product:publish:create')")
    public CommonResult<ProductPublishResponse> publishProduct(@Valid @RequestBody ProductPublishRequest request) {
        ProductPublishResponse response = null;
        try {
            response = productPublishService.publishProduct(request);
        } catch (ServiceException e) {
            log.error("PublishProduct Error", e);
            return CommonResult.error(e);
        }

        return CommonResult.success(response);
    }







}
