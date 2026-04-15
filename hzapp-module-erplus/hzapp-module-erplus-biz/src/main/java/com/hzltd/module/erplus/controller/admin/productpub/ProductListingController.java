package com.hzltd.module.erplus.controller.admin.productpub;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;
import com.hzltd.module.erplus.service.productpub.ErpProductPublishTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 跨境商品刊登流水")
@RestController
@RequestMapping("/erplus/product-listing")
@Validated
public class ProductListingController {

    @Resource
    private ErpProductPublishTaskService productPublishTaskService;

    @GetMapping("/get-latest-feedback")
    @Operation(summary = "获取最新刊登反馈")
    @Parameter(name = "taskId", description = "任务 ID", required = true)
    public CommonResult<ErpProductPublishTaskDO> getLatestFeedback(@RequestParam("taskId") Long taskId) {
        return success(productPublishTaskService.getProductPublishTask(taskId).orElse(null));
    }

    // 可以后续添加获取任务历史的分页方法
}
