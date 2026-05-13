package com.hzltd.module.amz.controller.admin.performance.vo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AmzBrandPerformanceStatsRespVO {

    private LocalDate startDate;

    private Long asinImpressionCount;

    private Long asinClickCount;

    private Long asinCartAddCount;

    private Long asinPurchaseCount;

    // Shipping breakdown for pie chart
    private Long samedayShippingPurchaseCount;
    private Long onedayShippingPurchaseCount;
    private Long twodayShippingPurchaseCount;
}
