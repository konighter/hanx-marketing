package com.hzltd.module.erplus.controller.admin.amzadv;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.amzadv.service.AmzAdvProfileService;
import com.hzltd.module.erplus.amzadv.service.AmzAdvReportService;
import com.hzltd.module.erplus.model.ApiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告 API")
@RestController
@RequestMapping("/erplus/amz-adv")
public class AmzAdvReportController {

    @Resource
    private AmzAdvProfileService amzAdvProfileService;

    @Resource
    private AmzAdvReportService amzAdvReportService;

    @GetMapping("/profiles")
    @Operation(summary = "获取广告 Profiles")
    public CommonResult<List<String>> listProfiles(@RequestParam("shopId") String shopId) {
        ApiRequest<Void> apiRequest = new ApiRequest<>();
        apiRequest.setShopId(shopId);
        return success(amzAdvProfileService.listProfiles(apiRequest));
    }

    @PostMapping("/reports/request")
    @Operation(summary = "发起报告请求")
    public CommonResult<String> requestReport(@RequestParam("shopId") String shopId,
            @RequestParam("profileId") String profileId,
            @RequestParam("reportType") String reportType,
            @RequestBody String bodyJson) {
        ApiRequest<Void> apiRequest = new ApiRequest<>();
        apiRequest.setShopId(shopId);
        return success(amzAdvReportService.requestReport(apiRequest, profileId, reportType, bodyJson));
    }

    @GetMapping("/reports/status")
    @Operation(summary = "查询报告状态")
    public CommonResult<JsonNode> getReportStatus(@RequestParam("shopId") String shopId,
            @RequestParam("profileId") String profileId,
            @RequestParam("reportId") String reportId) {
        ApiRequest<Void> apiRequest = new ApiRequest<>();
        apiRequest.setShopId(shopId);
        return success(amzAdvReportService.getReportStatus(apiRequest, profileId, reportId));
    }
}
