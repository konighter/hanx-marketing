package com.hzltd.module.amz.spapi.controller.admin.performance;

import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.amz.spapi.controller.admin.performance.vo.AmzBrandPerformanceStatsRespVO;
import com.hzltd.module.amz.dal.mysql.AmzBrandPerformanceReportMapper;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/amz/performance")
public class AmzBrandPerformanceController {

    @Resource
    private AmzBrandPerformanceReportMapper brandPerformanceReportMapper;

    @GetMapping("/asin-stats")
    public ApiResponse<List<AmzBrandPerformanceStatsRespVO>> getAsinStats(@RequestParam("asin") String asin) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        List<AmzBrandPerformanceStatsRespVO> stats = BeanUtils.toBean(brandPerformanceReportMapper.selectStatsByAsin(asin, startDate), AmzBrandPerformanceStatsRespVO.class);
        return ApiResponse.success(stats);
    }
}
