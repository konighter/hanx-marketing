package com.hzltd.module.erplus.adv.metadata.controller.admin.report;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.adv.metadata.service.report.AdsReportService;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceRespVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportTrendRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告绩效报表")
@RestController
@RequestMapping("/erplus/adv/report")
@Validated
public class AdsReportController {

    @Resource
    private AdsReportService adsReportService;

    @PostMapping("/scorecard")
    @Operation(summary = "获得核心指标卡片数据")
    @PreAuthorize("@ss.hasPermission('erplus:adv-report:query')")
    public CommonResult<AdsPerformanceRespVO> getPerformanceScorecard(@Valid @RequestBody AdsPerformanceReqVO reqVO) {
        return success(adsReportService.getPerformanceScorecard(reqVO));
    }

    @PostMapping("/trend")
    @Operation(summary = "获得指标趋势图数据")
    @PreAuthorize("@ss.hasPermission('erplus:adv-report:query')")
    public CommonResult<AdsReportTrendRespVO> getPerformanceTrend(@Valid @RequestBody AdsPerformanceReqVO reqVO) {
        return success(adsReportService.getPerformanceTrend(reqVO));
    }

    @PostMapping("/drilldown")
    @Operation(summary = "获得下钻层级数据")
    @PreAuthorize("@ss.hasPermission('erplus:adv-report:query')")
    public CommonResult<List<Map<String, Object>>> getPerformanceDrilldown(@Valid @RequestBody AdsPerformanceReqVO reqVO) {
        return success(adsReportService.getPerformanceDrilldown(reqVO));
    }

}
