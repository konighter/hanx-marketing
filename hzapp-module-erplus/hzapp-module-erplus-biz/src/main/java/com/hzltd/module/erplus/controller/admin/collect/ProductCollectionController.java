package com.hzltd.module.erplus.controller.admin.collect;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.collect.vo.ProductCollectionUploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@Slf4j
@RestController
@RequestMapping("/erplus/oversea/productCollection")
public class ProductCollectionController {


    @PermitAll
    @PostMapping("/upload")
    public CommonResult<Boolean> productUpload(@RequestBody ProductCollectionUploadVO productCollectionUploadVO) {
        log.info("productCollectionUploadVO:{}", productCollectionUploadVO);
        return CommonResult.success(true);
    }


    @RequestMapping("/update")
    public CommonResult<Boolean> productUpdate(@RequestBody ProductCollectionUploadVO productCollectionUploadVO) {

        return CommonResult.success(true);
    }


}
