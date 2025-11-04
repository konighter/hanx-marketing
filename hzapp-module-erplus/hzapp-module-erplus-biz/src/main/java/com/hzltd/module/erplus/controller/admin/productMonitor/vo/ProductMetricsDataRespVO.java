package com.hzltd.module.erplus.controller.admin.productMonitor.vo;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Map<String, BigDecimal> metricsData;

    @JsonAnyGetter
    public Map<String, BigDecimal> getMetricsData() {
        return metricsData;
    }

}
