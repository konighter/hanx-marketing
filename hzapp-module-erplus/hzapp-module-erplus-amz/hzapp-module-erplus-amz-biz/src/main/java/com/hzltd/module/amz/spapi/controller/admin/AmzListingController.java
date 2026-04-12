package com.hzltd.module.amz.spapi.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormConfigVO;
import com.hzltd.module.amz.spapi.service.AmazonListingSchemaService;
import com.hzltd.module.amz.spapi.service.AmazonListingSchemaServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理后台 - 亚马逊商品刊登")
@RestController
@RequestMapping("/erplus/amz/listing")
public class AmzListingController {

    @Resource
    private AmazonListingSchemaService schemaService;

    @Resource
    private AmazonListingSchemaServiceV2 schemaServiceV2;

//    @Operation(summary = "保存亚马逊刊登数据（本地存储+校验）")
//    @PostMapping("/save")
//    public CommonResult<Boolean> saveListing(@RequestBody Map<String, Object> req) {
//        Integer shopId = (Integer) req.get("shopId");
//        String marketId = (String) req.get("marketId");
//        String sku = (String) req.get("sku");
//        String productType = (String) req.get("productType");
//        @SuppressWarnings("unchecked")
//        Map<String, Object> attributes = (Map<String, Object>) req.get("attributes");
//
//        lifecycleService.saveListingLocally(shopId, marketId, sku, productType, attributes);
//        return CommonResult.success(true);
//    }

    @Operation(summary = "获取产品类型的表单配置（V2 流水线版）")
    @GetMapping("/schema")
    public CommonResult<AmzListingFormConfigVO> getSchema(@RequestParam("productType") String productType) {
        return CommonResult.success(schemaServiceV2.generateFormConfig(productType));
    }
}
