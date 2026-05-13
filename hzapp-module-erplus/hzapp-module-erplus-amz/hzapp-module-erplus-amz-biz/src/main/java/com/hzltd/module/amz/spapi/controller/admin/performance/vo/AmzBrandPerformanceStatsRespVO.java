package com.hzltd.module.amz.spapi.controller.admin.performance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AmzBrandPerformanceStatsRespVO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonProperty("impressionCount")
    private Long asinImpressionCount;

    @JsonProperty("clickCount")
    private Long asinClickCount;

    @JsonProperty("cartAddCount")
    private Long asinCartAddCount;

    @JsonProperty("purchaseCount")
    private Long asinPurchaseCount;

    // Shipping breakdown for pie chart
    @JsonProperty("sameDayShippingImpressionCount")
    private Long samedayShippingImpressionCount;
    @JsonProperty("oneDayShippingImpressionCount")
    private Long onedayShippingImpressionCount;
    @JsonProperty("twoDayShippingImpressionCount")
    private Long twodayShippingImpressionCount;

    @JsonProperty("sameDayShippingClickCount")
    private Long samedayShippingClickCount;
    @JsonProperty("oneDayShippingClickCount")
    private Long onedayShippingClickCount;
    @JsonProperty("twoDayShippingClickCount")
    private Long twodayShippingClickCount;

    @JsonProperty("sameDayShippingCartAddCount")
    private Long samedayShippingCartAddCount;
    @JsonProperty("oneDayShippingCartAddCount")
    private Long onedayShippingCartAddCount;
    @JsonProperty("twoDayShippingCartAddCount")
    private Long twodayShippingCartAddCount;

    @JsonProperty("sameDayShippingPurchaseCount")
    private Long samedayShippingPurchaseCount;
    @JsonProperty("oneDayShippingPurchaseCount")
    private Long onedayShippingPurchaseCount;
    @JsonProperty("twoDayShippingPurchaseCount")
    private Long twodayShippingPurchaseCount;
}
