package com.hzltd.module.erplus.controller.admin.productMonitor.vo;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductMetricsDataRespVO {

    /**
     * 日期(yyyyMMdd)
     */
    private Integer datekey;

    /**
     * 监控任务ID
     */
    private Long monitorId;
    /**
     * 产品ID
     */
    private String productId;

    /**
     * 指标值
     */
    @JsonUnwrapped
    private Map<String, BigDecimal> metricsData;







}
