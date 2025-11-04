package com.hzltd.module.erplus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.ProductMetricsDataRespVO;

import java.math.BigDecimal;
import java.util.Map;


public class Test {

    @org.junit.jupiter.api.Test
    public void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ProductMetricsDataRespVO respVO = new ProductMetricsDataRespVO();
        respVO.setDatekey(20230801);
        respVO.setMonitorId(100L);
        respVO.setProductId("100001");
        Map<String, BigDecimal> metrics = Maps.newHashMap();
        metrics.put("metric1", new BigDecimal("100"));
        metrics.put("metric2", new BigDecimal("200"));
        respVO.setMetricsData(metrics);

        System.out.println(mapper.writeValueAsString(respVO));

    }


}
