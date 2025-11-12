package com.hzltd.module.erplus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.ProductMetricsDataRespVO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static java.awt.SystemColor.menu;


public class TestService {

    @Test
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



    @Test
    public void Test2() {
       Map<String, String> data =  Maps.newHashMap(Map.of("SystemUser","ep:avatar",
               "SystemRole","ep:user",
               "SystemMenu","ep:menu",
               "SystemDept","fa:address-card",
               "SystemPost","fa:address-book-o",
               "SystemDictType","ep:collection",
               "SystemNotice","ep:takeaway-box",
               "SystemTokenClient","fa:key",
               "SystemOperateLog","ep:position",
               "SystemLoginLog","ep:promotion"
       ));
       data.putAll(Map.of("SystemSmsChannel","fa:stack-exchange",
               "SystemSmsTemplate","ep:connection",
               "SystemSmsLog","fa:edit",
               "SystemTenant","ep:house",
               "SystemTenantPackage","fa:bars",
               "SystemOAuth2Client","fa:hdd-o",
               "SystemArea","fa:map-marker",
               "SystemMailAccount","fa:universal-access",
               "SystemMailTemplate","fa:tag",
               "SystemMailLog","fa:edit"));
       data.putAll(Map.of(
               "SystemNotifyTemplate","fa:archive",
               "SystemNotifyMessage","fa:edit"));


        data.forEach((k,v)->{
            System.out.println("update system_menu set icon = '"+v+"' where component_name = '"+k+"';");
        });



    }

    @Test
    public void test3() {

    }




}
