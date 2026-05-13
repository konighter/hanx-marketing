package com.hzltd.module.amz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 亚马逊品牌表现报告 DO (覆盖 SCP/SQP)
 * 映射 Doris 表 amazon_brand_performance_report
 */
@TableName("amazon_brand_performance_report")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class AmzBrandPerformanceReportDO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long shopId;
    private String marketplaceId;
    private String reportType; // SCP or SQP
    private String asin;
    private String searchQuery;

    // 搜索热度
    private Long searchQueryScore;
    private Long searchQueryVolume;

    // 展现阶段
    private Long asinImpressionCount;
    private Long totalImpressionCount;
    private Double impressionShare;
    private Double impressionMedianPrice;
    private Long samedayShippingImpressionCount;
    private Long onedayShippingImpressionCount;
    private Long twodayShippingImpressionCount;

    // 点击阶段
    private Long asinClickCount;
    private Long totalClickCount;
    private Double asinClickShare;
    private Double asinClickRate;
    private Double totalClickRate;
    private Double asinMedianClickPrice;
    private Double totalMedianClickPrice;
    private Long samedayShippingClickCount;
    private Long onedayShippingClickCount;
    private Long twodayShippingClickCount;

    // 加购阶段
    private Long asinCartAddCount;
    private Long totalCartAddCount;
    private Double asinCartAddShare;
    private Double totalCartAddRate;
    private Double asinMedianCartAddPrice;
    private Double totalMedianCartAddPrice;
    private Long samedayShippingCartAddCount;
    private Long onedayShippingCartAddCount;
    private Long twodayShippingCartAddCount;

    // 购买阶段
    private Long asinPurchaseCount;
    private Long totalPurchaseCount;
    private Double asinPurchaseShare;
    private Double asinPurchaseSales;
    private Double asinConversionRate;
    private Double totalPurchaseRate;
    private Double asinMedianPurchasePrice;
    private Double totalMedianPurchasePrice;
    private Long samedayShippingPurchaseCount;
    private Long onedayShippingPurchaseCount;
    private Long twodayShippingPurchaseCount;

    private LocalDateTime updateTime;
}
