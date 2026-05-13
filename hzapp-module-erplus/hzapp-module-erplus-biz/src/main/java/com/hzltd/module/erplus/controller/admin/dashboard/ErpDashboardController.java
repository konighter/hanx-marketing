package com.hzltd.module.erplus.controller.admin.dashboard;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.dashboard.vo.ErpDashboardAlertRespVO;
import com.hzltd.module.erplus.controller.admin.dashboard.vo.ErpDashboardChartRespVO;
import com.hzltd.module.erplus.controller.admin.dashboard.vo.ErpDashboardSummaryRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - ERP 业务大盘")
@RestController
@RequestMapping("/erplus/dashboard")
public class ErpDashboardController {

    @GetMapping("/summary")
    @Operation(summary = "获取聚合 KPI 汇总")
    public CommonResult<ErpDashboardSummaryRespVO> getSummary() {
        ErpDashboardSummaryRespVO vo = new ErpDashboardSummaryRespVO();
        vo.setTotalOrders(1250L);
        vo.setActiveProducts(450L);
        vo.setTotalInventory(8500L);
        vo.setSellThroughRate(new BigDecimal("0.75"));
        vo.setActiveStores(12);
        return success(vo);
    }

    @GetMapping("/alerts")
    @Operation(summary = "获取业务预警列表")
    public CommonResult<List<ErpDashboardAlertRespVO>> getAlerts() {
        List<ErpDashboardAlertRespVO> alerts = new ArrayList<>();
        
        ErpDashboardAlertRespVO a1 = new ErpDashboardAlertRespVO();
        a1.setType("SALES_DROP");
        a1.setTitle("销量大幅下滑");
        a1.setValue("85");
        a1.setDelta(new BigDecimal("-0.22"));
        a1.setMessage("TikTok Shop (UK) 近24小时销量异常下降");
        alerts.add(a1);

        ErpDashboardAlertRespVO a2 = new ErpDashboardAlertRespVO();
        a2.setType("STOCK_LOW");
        a2.setTitle("库存预警");
        a2.setValue("15");
        a2.setDelta(new BigDecimal("-0.05"));
        a2.setMessage("5个核心 SKU 预计在 3 天内断货");
        alerts.add(a2);

        ErpDashboardAlertRespVO a3 = new ErpDashboardAlertRespVO();
        a3.setType("ACOS_HIGH");
        a3.setTitle("广告费用飙升");
        a3.setValue("45%");
        a3.setDelta(new BigDecimal("0.15"));
        a3.setMessage("Amazon 美国站广告 ACOS 突破目标值 30%");
        alerts.add(a3);

        return success(alerts);
    }

    @GetMapping("/charts")
    @Operation(summary = "获取图表分析数据")
    public CommonResult<List<ErpDashboardChartRespVO>> getCharts() {
        List<ErpDashboardChartRespVO> charts = new ArrayList<>();

        // 1. GMV Trend
        ErpDashboardChartRespVO c1 = new ErpDashboardChartRespVO();
        c1.setName("多店铺 GMV 趋势");
        c1.setLabels(Arrays.asList("05-07", "05-08", "05-09", "05-10", "05-11", "05-12", "05-13"));
        
        ErpDashboardChartRespVO.Dataset d1 = new ErpDashboardChartRespVO.Dataset();
        d1.setLabel("TikTok Shop");
        d1.setData(Arrays.asList(1200, 1500, 1100, 1800, 2100, 1900, 2300));
        
        ErpDashboardChartRespVO.Dataset d2 = new ErpDashboardChartRespVO.Dataset();
        d2.setLabel("Shopee");
        d2.setData(Arrays.asList(800, 900, 950, 880, 1050, 1100, 1200));

        c1.setDatasets(Arrays.asList(d1, d2));
        charts.add(c1);

        return success(charts);
    }
}
